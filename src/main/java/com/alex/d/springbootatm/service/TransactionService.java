package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.model.CardATM;
import com.alex.d.springbootatm.model.TransactionATM;
import com.alex.d.springbootatm.repository.CardATMRepository;
import com.alex.d.springbootatm.repository.TransactionRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepo transactionRepo;
    private final CardATMRepository cardATMRepository;


    public TransactionService(TransactionRepo transactionRepo, CardATMRepository cardATMRepository) {
        this.transactionRepo = transactionRepo;
        this.cardATMRepository = cardATMRepository;
    }

    @Transactional
    public void sendTransaction(CardATM senderCard, CardATM recipientCard, BigDecimal amount) {
        // Создание новой транзакции
        TransactionATM transaction = new TransactionATM();

        // Уменьшение баланса отправителя
        BigDecimal newSenderBalance = senderCard.getBalance().subtract(amount);
        senderCard.setBalance(newSenderBalance);

        // Увеличение баланса получателя
        BigDecimal newRecipientBalance = recipientCard.getBalance().add(amount);
        recipientCard.setBalance(newRecipientBalance);

        // Создание и сохранение транзакции в базе данных
        transaction.setTransactionType("SEND");
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setSenderCard(senderCard);

        transaction.setRecipientCard(recipientCard);
        saveTransaction(transaction);

        // Обновление карт в базе данных
        cardATMRepository.save(senderCard);
        cardATMRepository.save(recipientCard);

        // Сохраняем транзакцию в базе данных
        saveTransaction(transaction);
    }



//    public void sendTransaction(CardATM senderCardNumber, CardATM recipientCardNumber, BigDecimal amount) throws InsufficientFundsException {
//        // Получение данных о карте отправителя из базы данных
//        Optional<CardATM> senderCardOptional = Optional.ofNullable(cardATMRepository.findByCardNumber(String.valueOf(senderCardNumber)));
//        if (senderCardOptional.isEmpty()) {
//            throw new IllegalArgumentException("Sender card not found.");
//        }
//        CardATM senderCard = senderCardOptional.get();
//
//        // Получение данных о карте получателя из базы данных
//        Optional<CardATM> recipientCardOptional = Optional.ofNullable(cardATMRepository.findByCardNumber(String.valueOf(recipientCardNumber)));
//        if (recipientCardOptional.isEmpty()) {
//            throw new IllegalArgumentException("Recipient card not found.");
//        }
//        CardATM recipientCard = recipientCardOptional.get();
//
//        // Проверка наличия достаточных средств на карте отправителя
//        if (senderCard.getBalance().compareTo(amount) < 0) {
//            throw new InsufficientFundsException("Insufficient funds on sender's card.");
//        }
//
//        // Уменьшение баланса отправителя
//        BigDecimal newSenderBalance = senderCard.getBalance().subtract(amount);
//        senderCard.setBalance(newSenderBalance);
//
//        // Увеличение баланса получателя
//        BigDecimal newRecipientBalance = recipientCard.getBalance().add(amount);
//        recipientCard.setBalance(newRecipientBalance);
//
//        // Создание и сохранение транзакции в базе данных
//        TransactionATM transaction = new TransactionATM();
//        transaction.setTransactionType("Type");
//        transaction.setAmount(amount);
//        transaction.setTimestamp(LocalDateTime.now());
//        transaction.setSenderCard(senderCard);
//        transaction.setRecipientCard(recipientCard);
//        saveTransaction(transaction);
//
//        // Обновление карт в базе данных
//        cardATMRepository.save(senderCard);
//        cardATMRepository.save(recipientCard);
//    }

    @Transactional
    public void saveTransaction(TransactionATM transaction) {
        transactionRepo.save(transaction);
    }


    @Transactional
    public CardATM deposit(String cardNumber, BigDecimal amount) throws CardNotFoundException {
        CardATM card = cardATMRepository.findByCardNumber(cardNumber);

        // Увеличение баланса карты
        BigDecimal newBalance = card.getBalance().add(amount);
        card.setBalance(newBalance);

        // Создание и сохранение транзакции
        TransactionATM transaction = new TransactionATM();
        transaction.setTransactionType("DEPOSIT");
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setSenderCard(card);
        transactionRepo.save(transaction);

        // Обновление карты в базе данных
        cardATMRepository.save(card);

        return card;
    }
}
