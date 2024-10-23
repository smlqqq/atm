package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.dto.BankCardDTO;
import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.kafka.KafkaProducerService;
import com.alex.d.springbootatm.kafka.KafkaTopic;
import com.alex.d.springbootatm.model.AtmModel;
import com.alex.d.springbootatm.model.BankCardModel;
import com.alex.d.springbootatm.model.TransactionModel;
import com.alex.d.springbootatm.repository.ATMRepository;
import com.alex.d.springbootatm.repository.BankCardRepository;
import com.alex.d.springbootatm.repository.TransactionRepository;
import com.alex.d.springbootatm.response.*;
import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class AtmServiceImpl implements AtmService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private BankCardRepository bankCardRepository;
    @Autowired
    private ATMRepository atmRepository;
    @Autowired
    private Gson gson;
    @Autowired
    private KafkaProducerService kafkaProducerService;


    @Override
    public TransactionResponse processTransaction(String cardNumber, BigDecimal amount, boolean isDeposit) {
        Optional<BankCardModel> optCard = bankCardRepository.findByCardNumber(cardNumber);

        if (optCard.isPresent()) {
            BankCardModel card = optCard.get();

            BigDecimal cardBlance = isDeposit
                    ? card.getBalance().add(amount)
                    : card.getBalance().subtract(amount);

            card.setBalance(cardBlance);

            String transactionType = isDeposit ? "DEPOSIT_FROM" : "WITHDRAW";
            TransactionModel transactionModel = TransactionModel.builder()
                    .transactionType(transactionType)
                    .amount(amount)
                    .timestamp(LocalDateTime.now())
                    .senderAtmModel(returnAtmName())
                    .recipientCard(card)
                    .recipientBalanceAfter(cardBlance)
                    .build();

            // Сохраняем транзакцию
            transactionRepository.save(transactionModel);

            log.info("{} of {} for card {} was successful. Balance: {}",
                    isDeposit ? "Deposit" : "Withdrawal", amount, cardNumber, cardBlance);

            setKafkaProducerService(
                    BankCardDTO.builder()
                            .cardNumber(optCard.get().getCardNumber())
                            .balance(cardBlance)
                            .build(),
                    KafkaTopic.ATM_TOPIC
            );
            if (isDeposit) {
                return new DepositResponse(cardNumber, cardBlance);
            } else {
                return new WithdrawResponse(cardNumber, amount, cardBlance);
            }
        } else {
            log.warn("Attempted {} for non-existing card: {}", isDeposit ? "deposit" : "withdrawal", cardNumber);
            throw new CardNotFoundException("Card not found with number: " + cardNumber);
        }
    }

    @Override
    @Transactional
    public TransferResponse sendTransaction(String senderCard, String recipientCard, BigDecimal amount) {

        Optional<BankCardModel> optSenderCard = bankCardRepository.findByCardNumber(senderCard);
        Optional<BankCardModel> optRecipientCard = bankCardRepository.findByCardNumber(recipientCard);

        if (optSenderCard.isPresent() && optRecipientCard.isPresent()) {
            // Update sender's balance
            BigDecimal newSenderBalance = optSenderCard.get().getBalance().subtract(amount);
            optSenderCard.get().setBalance(newSenderBalance);
            // Update recipient's balance
            BigDecimal newRecipientBalance = optRecipientCard.get().getBalance().add(amount);
            optRecipientCard.get().setBalance(newRecipientBalance);
            // Create a new transaction
            TransactionModel transactionModel = TransactionModel.builder()
                    .transactionType("SEND")
                    .amount(amount)
                    .timestamp(LocalDateTime.now())
                    .senderCard(optSenderCard.get())
                    .recipientCard(optRecipientCard.get())
                    // Set balances after transaction in the transaction model
                    .senderBalanceAfter(newSenderBalance)
                    .recipientBalanceAfter(newRecipientBalance)
                    .build();

            transactionRepository.save(transactionModel);
            // Save updated sender and recipient cards
            bankCardRepository.save(optSenderCard.get());
            bankCardRepository.save(optRecipientCard.get());
            log.info("Transaction completed: Sender card {} balance {}, Recipient card {} balance {}, Amount {}",
                    optSenderCard.get().getCardNumber(), newSenderBalance, optRecipientCard.get().getCardNumber(), newRecipientBalance, amount);

            return new TransferResponse(senderCard, recipientCard, amount, newSenderBalance, newRecipientBalance);
        } else {

            if (optSenderCard.isEmpty()) {
                log.warn("Sender card not found: {}", senderCard);
                throw new CardNotFoundException("Sender card not found with number: " + senderCard);
            }

            if (optRecipientCard.isEmpty()) {
                log.warn("Recipient card not found: {}", recipientCard);
                throw new CardNotFoundException("Recipient card not found with number: " + recipientCard);
            }
        }

        return null;
    }

    @Override
    @Transactional
    public BankCardModel saveCreatedCardToDB() {

        BankCardModel cardModel = BankCardModel.builder()
                .cardNumber(generateCreditCardNumber())
                .pinNumber(hashPinCode(generatePinCode()))
                .balance(generateBalance())
                .build();

        log.info("Card saved into db {} pin code {}", cardModel.getCardNumber(), cardModel.getPinNumber());

        setKafkaProducerService(
                BankCardDTO.builder()
                        .cardNumber(cardModel.getCardNumber())
                        .pinCode(cardModel.getPinNumber())
                        .build(),
                KafkaTopic.KAFKA_MANAGER_TOPIC);

        return bankCardRepository.save(cardModel);
    }

    @Override
    public String generateCreditCardNumber() {
        StringBuilder sb = new StringBuilder("400000");
        for (int i = 1; i < 10; i++) {
            sb.append((int) (Math.random() * 10));
        }
        String prefix = sb.toString();
        int checksum = LuhnsAlgorithm.calculateChecksum(prefix);
        sb.append(checksum);
        return sb.toString();
    }

    @Override
    public BigDecimal generateBalance() {
        return BigDecimal.valueOf(0);
    }

    @Override
    public BalanceResponse checkBalanceByCardNumber(String cardNumber) {

        Optional<BankCardModel> optCard = bankCardRepository.findByCardNumber(cardNumber);

        if (optCard.isPresent()) {
            BankCardModel card = optCard.get();
            BigDecimal cardBalance = card.getBalance();
            log.info("Card: {} Balance: {}", cardNumber, cardBalance);

            setKafkaProducerService(
                    BankCardDTO.builder()
                            .cardNumber(optCard.get().getCardNumber())
                            .balance(cardBalance)
                            .build(),
                    KafkaTopic.ATM_TOPIC
            );

            return new BalanceResponse(cardNumber, cardBalance);
        } else
            throw new CardNotFoundException("Card not found: " + cardNumber);

    }


    @Override
    public AtmModel returnAtmName() {
        Random random = new Random();
        List<AtmModel> allAtmModelNames = atmRepository.findAll();
        int randomIndex = random.nextInt(allAtmModelNames.size());
        return allAtmModelNames.get(randomIndex);
    }

    @Override
    public String hashPinCode(String pinCode) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(pinCode);
    }

    @Override
    public String generatePinCode() {
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }

    public void setKafkaProducerService(Object data, String topic) {
        String message = gson.toJson(data);
        kafkaProducerService.sendMessage(topic, message);
    }

}
