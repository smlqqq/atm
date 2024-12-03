package com.alex.d.springbootatm.service.atm;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.messaging.KafkaProducerService;
import com.alex.d.springbootatm.messaging.KafkaTopic;
import com.alex.d.springbootatm.model.AtmModel;
import com.alex.d.springbootatm.model.CardModel;
import com.alex.d.springbootatm.model.TransactionModel;
import com.alex.d.springbootatm.model.dto.CardDto;
import com.alex.d.springbootatm.model.dto.response.*;
import com.alex.d.springbootatm.repository.AtmRepository;
import com.alex.d.springbootatm.repository.CardRepository;
import com.alex.d.springbootatm.repository.TransactionRepository;
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
    public CardModel fetchCardFromDb(String card) {
        return cardRepository.findByCardNumber(card)
                .orElseThrow(() -> new CardNotFoundException("Card not found: " + card));
    }

    @Override
    @Transactional
    public void addAmountToBalance(String card, BigDecimal amount) {
        cardRepository.addBalance(card, amount);
    }

    @Override
    @Transactional
    public void subtractAmountFromBalance(String card, BigDecimal amount) {
        cardRepository.subtractBalance(card, amount);
    }

    @Override
    public BigDecimal addOrSubtractBalance(String cardNumber, BigDecimal amount, boolean addAmount) {
        if (addAmount) {
            addAmountToBalance(cardNumber, amount);
        } else {
            subtractAmountFromBalance(cardNumber, amount);
        }
        return checkBalanceByCardNumber(cardNumber).getBalance();
    }


    @Override
    public CardResponse updateAccountBalance(String cardNumber, BigDecimal amount, boolean isDeposit) {
        CardModel card = fetchCardFromDb(cardNumber);

        if (card != null) {

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
                            .pin("***")
                            .build(),
                    KafkaTopic.ATM_TOPIC.getTopicName()
            );

            if (!isDeposit) {

                return WithdrawResponse.builder()
                        .cardNumber(cardNumber)
                        .withdrawAmount(amount.toPlainString())
                        .build();
            } else {

                return DepositeResponse.builder()
                        .cardNumber(cardNumber)
                        .depositAmount(amount.toPlainString())
                        .build();
            }
        } else {
            log.warn("Attempted {} for non-existing card: {}", isDeposit ? "deposit" : "withdrawal", cardNumber);
            throw new CardNotFoundException("Card not found with number: " + cardNumber);
        }
    }


    @Override
    @Transactional
    public TransactionResponse transferBetweenCards(String senderCard, String recipientCard, BigDecimal amount) {

        CardModel senderModel = fetchCardFromDb(senderCard);
        CardModel recipientModel = fetchCardFromDb(recipientCard);


        if (senderModel != null && recipientModel != null) {

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

            return TransactionResponse.builder()
                    .senderCardNumber(senderCard)
                    .recipientCardNumber(recipientCard)
                    .transferredFunds(amount)
                    .senderBalance(balanceAfterSubtract)
                    .recipientBalance(newRecipientBalance)
                    .build();
        }

        log.error("card not found");
        return null;
    }


    @Override
    public BalanceResponse checkBalanceByCardNumber(String cardNumber) {
        CardModel card = fetchCardFromDb(cardNumber);
        BigDecimal balance = card.getBalance();
        return BalanceResponse.builder()
                .cardNumber(cardNumber)
                .balance(balance)
                .build();
    }

    @Override
    public AtmModel returnAtmName() {
        Random random = new Random();
        List<AtmModel> allAtmModelNames = atmRepository.findAll();
        int randomIndex = random.nextInt(allAtmModelNames.size());
        return allAtmModelNames.get(randomIndex);
    }


}
