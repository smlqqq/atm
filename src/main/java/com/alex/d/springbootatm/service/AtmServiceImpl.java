package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.dto.CardDto;
import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.kafka.KafkaProducerService;
import com.alex.d.springbootatm.kafka.KafkaTopic;
import com.alex.d.springbootatm.model.AtmModel;
import com.alex.d.springbootatm.model.BankCardModel;
import com.alex.d.springbootatm.model.TransactionModel;
import com.alex.d.springbootatm.repository.AtmRepository;
import com.alex.d.springbootatm.repository.CardRepository;
import com.alex.d.springbootatm.repository.TransactionRepository;
import com.alex.d.springbootatm.response.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class AtmServiceImpl implements AtmService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private AtmRepository atmRepository;
    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Override
    public BankCardModel fetchCardModel(String card) {
        return cardRepository.findByCardNumber(card)
                .orElseThrow(() -> new CardNotFoundException("Card not found" + card));
    }

    @Override
    public String fetchCardNumberAsString(String card) {
        return cardRepository.findByCardNumber(card)
                .map(BankCardModel::getCardNumber)
                .orElseThrow(() -> new CardNotFoundException("Card not found: " + card));
    }

    public BigDecimal addAmountToBalance(String card, BigDecimal amount) {
        return cardRepository.addBalance(card, amount);
    }

    public BigDecimal subtractAmountFromBalance(String card, BigDecimal amount) {
        return cardRepository.subtractBalance(card, amount);
    }

    public BigDecimal addOrSubtractBalance(String cardNumber, BigDecimal amount, boolean addAmount) {
        return addAmount
                ? addAmountToBalance(cardNumber, amount)
                : subtractAmountFromBalance(cardNumber, amount);
    }

    @Override
    public TransactionResponse processTransaction(String cardNumber, BigDecimal amount, boolean isDeposit) {

        if (fetchCardNumberAsString(cardNumber) != null) {

            BankCardModel card = fetchCardModel(cardNumber);

            BigDecimal cardBalance = addOrSubtractBalance(cardNumber, amount, isDeposit);

            String transactionType = isDeposit ? "DEPOSIT_FROM" : "WITHDRAW";
            TransactionModel transactionModel = TransactionModel.builder()
                    .transactionType(transactionType)
                    .amount(amount)
                    .timestamp(LocalDateTime.now())
                    .senderAtmModel(returnAtmName())
                    .recipientCard(card)
                    .recipientBalanceAfter(cardBalance)
                    .build();

            transactionRepository.save(transactionModel);

            log.info("{} of {} for card {} was successful. Balance: {}",
                    isDeposit ? "Deposit" : "Withdrawal", amount, cardNumber, cardBalance);

            kafkaProducerService.setKafkaProducerServiceMessage(
                    CardDto.builder()
                            .cardNumber(card.getCardNumber())
                            .balance(cardBalance)
                            .build(),
                    KafkaTopic.ATM_TOPIC
            );
            if (isDeposit) {
                return new DepositResponse(cardNumber, cardBalance);
            } else {
                return new WithdrawResponse(cardNumber, amount, cardBalance);
            }
        } else {
            log.warn("Attempted {} for non-existing card: {}", isDeposit ? "deposit" : "withdrawal", cardNumber);
            throw new CardNotFoundException("Card not found with number: " + cardNumber);
        }
    }

    @Override
    @Transactional
    public TransferResponse sendTransaction(String senderCard, String recipientCard, BigDecimal amount) {

        if (fetchCardNumberAsString(senderCard) != null && fetchCardNumberAsString(recipientCard) != null) {

            BankCardModel senderModel = fetchCardModel(senderCard);
            BankCardModel recipientModel = fetchCardModel(recipientCard);

            // Update sender's balance
            BigDecimal balanceAfterSubtract = addOrSubtractBalance(senderCard, amount, false);

            // Update recipient's balance
            BigDecimal newRecipientBalance = addOrSubtractBalance(recipientCard, amount, true);

            // Create a new transaction
            TransactionModel transactionModel = TransactionModel.builder()
                    .transactionType("SEND")
                    .amount(amount)
                    .timestamp(LocalDateTime.now())
                    .senderCard(senderModel)
                    .recipientCard(recipientModel)
                    // Set balances after transaction in the transaction model
                    .senderBalanceAfter(balanceAfterSubtract)
                    .recipientBalanceAfter(newRecipientBalance)
                    .build();

            transactionRepository.save(transactionModel);
            // Save updated sender and recipient cards
            cardRepository.save(senderModel);
            cardRepository.save(recipientModel);
            log.info("Transaction completed: Sender card {} balance {}, Recipient card {} balance {}, Amount {}",
                    senderModel.getCardNumber(), balanceAfterSubtract, recipientModel.getCardNumber(), newRecipientBalance, amount);

            return new TransferResponse(senderCard, recipientCard, amount, balanceAfterSubtract, newRecipientBalance);
        }

        log.error("card number not found");
        return null;
    }


    @Override
    public BalanceResponse checkBalanceByCardNumber(String cardNumber) {
        BankCardModel cardModel = fetchCardModel(cardNumber);
        BigDecimal cardBalance = cardRepository.getBankCardBalanceByCardNumber(String.valueOf(cardModel)).getBalance();
        return new BalanceResponse(cardNumber, cardBalance);
    }

    @Override
    public AtmModel returnAtmName() {
        Random random = new Random();
        List<AtmModel> allAtmModelNames = atmRepository.findAll();
        int randomIndex = random.nextInt(allAtmModelNames.size());
        return allAtmModelNames.get(randomIndex);
    }

}
