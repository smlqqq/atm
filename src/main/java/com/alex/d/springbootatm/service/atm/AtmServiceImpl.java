package com.alex.d.springbootatm.service.atm;

import com.alex.d.springbootatm.dto.BankCardDto;
import com.alex.d.springbootatm.messaging.KafkaProducerService;
import com.alex.d.springbootatm.messaging.KafkaTopic;
import com.alex.d.springbootatm.model.Atm;
import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.model.BankCardTransaction;
import com.alex.d.springbootatm.model.response.*;
import com.alex.d.springbootatm.repository.AtmRepository;
import com.alex.d.springbootatm.repository.CardRepository;
import com.alex.d.springbootatm.repository.TransactionRepository;
import com.alex.d.springbootatm.service.card.BankCardService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class AtmServiceImpl implements AtmService {

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final AtmRepository atmRepository;
    private final KafkaProducerService kafkaProducerService;
    private final BankCardService cardService;

    public AtmServiceImpl(TransactionRepository transactionRepository, CardRepository cardRepository, AtmRepository atmRepository, KafkaProducerService kafkaProducerService, BankCardService cardService) {
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
        this.atmRepository = atmRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.cardService = cardService;
    }


//    public BigDecimal fetchBankCardBalance(String cardNumber) {
//        BankCard card = fetchCardFromDb(cardNumber);
//        return card.getBalance();
//    }

    @Override
    @Transactional
    public BigDecimal addAmountToBankCardBalance(String card, BigDecimal amount) {
        cardRepository.addBalance(card, amount);
        return amount;
    }

    @Override
    @Transactional
    public BigDecimal subtractAmountFromBankCardBalance(String card, BigDecimal amount) {
        cardRepository.subtractBalance(card, amount);
        return amount;
    }

    @Override
    public BigDecimal addOrSubtractAmountFromBalance(String cardNumber, BigDecimal amount, boolean addAmount) {
        return addAmount ? addAmountToBankCardBalance(cardNumber, amount) : subtractAmountFromBankCardBalance(cardNumber, amount);
    }

    @Override
    public CardResponse processAtmTransaction(String cardNumber, BigDecimal amount, String transactionType) {
        return transactionType.equals(TransactionType.DEPOSIT.getTransactionType()) ? processDeposit(cardNumber, amount) : processWithdrawal(cardNumber, amount);
    }

    @Override
    public DepositeResponse processDeposit(String cardNumber, BigDecimal amount) {

        BankCard card = cardService.fetchCardFromDb(cardNumber);

        BigDecimal cardBalance = addAmountToBankCardBalance(cardNumber, amount);

        BankCardTransaction transaction = processAtmTransaction(TransactionType.DEPOSIT.getTransactionType(), amount, card, cardBalance);

        saveNewTransaction(transaction);

        log.info("{} of {} for card {} was successful. Balance: {}",
                TransactionType.DEPOSIT.getTransactionType(), amount, cardNumber, cardBalance);

        messageToKafka(card, cardBalance, KafkaTopic.ATM_TOPIC.getTopicName());

        return DepositeResponse.builder()
                .cardNumber(cardNumber)
                .depositAmount(amount.toPlainString())
                .build();
    }

    @Override
    public WithdrawResponse processWithdrawal(String cardNumber, BigDecimal amount) {

        BankCard card = cardService.fetchCardFromDb(cardNumber);

        BigDecimal cardBalance = subtractAmountFromBankCardBalance(cardNumber, amount);

        BankCardTransaction transaction = processAtmTransaction(TransactionType.WITHDRAW.getTransactionType(), amount, card, cardBalance);

        saveNewTransaction(transaction);

        log.info("{} of {} for card {} was successful. Balance: {}",
                TransactionType.WITHDRAW.getTransactionType(), amount, cardNumber, cardBalance);

        messageToKafka(card, cardBalance, KafkaTopic.ATM_TOPIC.getTopicName());

        return WithdrawResponse.builder()
                .cardNumber(cardNumber)
                .withdrawAmount(amount.toPlainString())
                .build();
    }


    @Override
    @Transactional
    public TransactionResponse processCardTransaction(String senderCard, String recipientCard, BigDecimal amount) {

        BankCard sender = cardService.fetchCardFromDb(senderCard);

        BankCard recipient = cardService.fetchCardFromDb(recipientCard);

        // Update sender's balance
        BigDecimal balanceAfterSubtract = addOrSubtractAmountFromBalance(senderCard, amount, false);
        // Update recipient's balance
        BigDecimal newRecipientBalance = addOrSubtractAmountFromBalance(recipientCard, amount, true);
        BankCardTransaction cardTransaction = processCardTransaction(TransactionType.SEND.getTransactionType(), amount, sender, recipient, balanceAfterSubtract, newRecipientBalance);
        saveNewTransaction(cardTransaction);
        // Save updated sender and recipient cards
        saveUpdatedBankCardData(sender);
//            cardRepository.save(recipient);
        saveUpdatedBankCardData(recipient);

        log.info("Transaction completed: Sender card {} balance {}, Recipient card {} balance {}, Amount {}",
                sender.getCardNumber(), balanceAfterSubtract, recipient.getCardNumber(), newRecipientBalance, amount);

        return TransactionResponse.builder()
                .senderCardNumber(senderCard)
                .recipientCardNumber(recipientCard)
                .transferredFunds(amount)
                .senderBalance(balanceAfterSubtract)
                .recipientBalance(newRecipientBalance)
                .build();

    }

    public BankCardTransaction processCardTransaction(String transactionType, BigDecimal amount, BankCard sender, BankCard recipient, BigDecimal senderBalanceAfterSubtract, BigDecimal newRecipientBalance) {
        validateParams(transactionType, amount, sender, recipient, senderBalanceAfterSubtract);
        // Create a new transaction
        return initBuilder(transactionType, amount)
                .senderCard(sender)
                .recipientCard(recipient)
                .senderBalanceAfter(senderBalanceAfterSubtract)
                .recipientBalanceAfter(newRecipientBalance)
                .build();

//        return BankCardTransaction.builder()
//                .transactionType(transactionType)
//                .amount(amount)
//                .timestamp(LocalDateTime.now())
//                .senderCard(sender)
//                .recipientCard(recipient)
//                // Set balances after transaction in the transaction model
//                .senderBalanceAfter(senderBalanceAfterSubtract)
//                .recipientBalanceAfter(newRecipientBalance)
//                .build();
    }

    private BankCardTransaction.BankCardTransactionBuilder initBuilder(String transactionType, BigDecimal amount) {
        return BankCardTransaction.builder()
                .transactionType(transactionType)
                .amount(amount)
                .timestamp(LocalDateTime.now());
    }

    private void validateParams(String transactionType, BigDecimal amount, BankCard sender,
                                BankCard recipient, BigDecimal senderBalance) {
        if (transactionType == null || amount == null || sender == null || recipient == null
                || senderBalance == null) {
            log.error("Invalid transaction parameters");
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        if (senderBalance.compareTo(BigDecimal.ZERO) < 0) {
            log.error("Sender balance is less than zero");
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Amount is less than zero");
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    public BankCardTransaction processAtmTransaction(
            String transactionType,
            BigDecimal amount,
            BankCard card,
            BigDecimal recipientBalanceAfter) {

        validateParams(transactionType, amount, card, recipientBalanceAfter);

        return initBuilder(transactionType, amount)
                .senderAtm(returnAtmName())
                .recipientCard(card)
                .senderBalanceAfter(null) // Явное указание, если требуется
                .recipientBalanceAfter(recipientBalanceAfter)
                .build();
    }

    private void validateParams(String transactionType, BigDecimal amount,
                                BankCard card, BigDecimal recipientBalance) {
        if (transactionType == null || amount == null || card == null || recipientBalance == null) {
            log.error("Invalid transaction parameters");
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Amount is less than zero");
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

//    public BankCardTransaction processAtmTransaction(String transactionType, BigDecimal amount, BankCard card, BigDecimal cardBalance) {
//        // Create a new transaction
//        return BankCardTransaction.builder()
//                .transactionType(transactionType)
//                .amount(amount)
//                .timestamp(LocalDateTime.now())
//                .senderAtm(returnAtmName())
//                .recipientCard(card)
//                .recipientBalanceAfter(cardBalance)
//                .build();
//    }

    @Override
    public BalanceResponse checkBalanceByCardNumber(String cardNumber) {
        BankCard card = cardService.fetchCardFromDb(cardNumber);
        BigDecimal balance = card.getBalance();
        return BalanceResponse.builder()
                .cardNumber(cardNumber)
                .balance(balance)
                .build();
    }


    @Override
    public Atm returnAtmName() {
        Random random = new Random();
        List<Atm> allAtmNames = atmRepository.findAll();
        int randomIndex = random.nextInt(allAtmNames.size());
        return allAtmNames.get(randomIndex);
    }

    private <S extends BankCardTransaction> void saveNewTransaction(S entity) {
        transactionRepository.save(entity);
    }

    private <S extends BankCard> void saveUpdatedBankCardData(S entity) {
        cardRepository.save(entity);
    }

    private void messageToKafka(BankCard cardNum, BigDecimal cardBalance, String topic) {
        kafkaProducerService.setKafkaProducerServiceMessage(BankCardDto.builder()
                .cardNumber(cardNum.getCardNumber())
                .balance(cardBalance)
                .build(), topic);
    }

}
