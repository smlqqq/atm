package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.model.ATM;
import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.model.Transaction;
import com.alex.d.springbootatm.repository.ATMRepository;
import com.alex.d.springbootatm.repository.CardATMRepository;
import com.alex.d.springbootatm.repository.TransactionRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class TransactionService {

    private final TransactionRepo transactionRepo;
    private final CardATMRepository cardATMRepository;
    private final ATMRepository atmRepository;

    private final Random random = new Random();


    public TransactionService(TransactionRepo transactionRepo, CardATMRepository cardATMRepository, ATMRepository atmRepository) {
        this.transactionRepo = transactionRepo;
        this.cardATMRepository = cardATMRepository;
        this.atmRepository = atmRepository;
    }

    @Transactional
    public void saveTransaction(Transaction transaction) {
        transactionRepo.save(transaction);
    }

    @Transactional
    public void sendTransaction(Optional<BankCard> optionalSenderCard, Optional<BankCard> optionalRecipientCard, BigDecimal amount) throws CardNotFoundException {
        if (optionalSenderCard.isPresent() && optionalRecipientCard.isPresent()) {
            BankCard senderCard = optionalSenderCard.get();
            BankCard recipientCard = optionalRecipientCard.get();

            // Create a new transaction
            Transaction transaction = new Transaction();
            transaction.setTransactionType("SEND");
            transaction.setAmount(amount);
            transaction.setTimestamp(LocalDateTime.now());
            transaction.setSenderCard(senderCard);
            transaction.setRecipientCard(recipientCard);
            saveTransaction(transaction);

            // Update sender's balance
            BigDecimal newSenderBalance = senderCard.getBalance().subtract(amount);
            senderCard.setBalance(newSenderBalance);

            // Update recipient's balance
            BigDecimal newRecipientBalance = recipientCard.getBalance().add(amount);
            recipientCard.setBalance(newRecipientBalance);

            // Save updated sender and recipient cards
            cardATMRepository.save(senderCard);
            cardATMRepository.save(recipientCard);
        } else {
            throw new CardNotFoundException("Sender card or recipient card not found");
        }
    }

    @Transactional
    public void depositFromATM(Optional<BankCard> optionalRecipientCard, BigDecimal amount) throws CardNotFoundException {
        BankCard recipientCard = optionalRecipientCard.orElseThrow(() -> new CardNotFoundException("Recipient card not found."));

        // Увеличение баланса карты
        BigDecimal newBalance = recipientCard.getBalance().add(amount);
        recipientCard.setBalance(newBalance);

        // Случайное имя банкомата из списка уже имеющихся
        List<ATM> allAtmNames = atmRepository.findAll(); // Предполагается, что у вас есть метод в atmRepository для получения всех имен банкоматов
        // Генерация случайного индекса для выбора случайного имени банкомата

        int randomIndex = random.nextInt(allAtmNames.size());
        // Получение случайного имени банкомата
        ATM randomAtmName = allAtmNames.get(randomIndex);

        // Создание и сохранение транзакции
        Transaction transaction = new Transaction();
        transaction.setTransactionType("DEPOSIT_FROM_ATM");
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setSenderATM(randomAtmName);
        transaction.setRecipientCard(recipientCard);

        transactionRepo.save(transaction);
    }


    @Transactional
    public void withdrawFromATM(Optional<BankCard> card, BigDecimal amount) throws CardNotFoundException {
        BankCard cardNumber = card.orElseThrow(() -> new CardNotFoundException("Card not found."));

        BigDecimal newBalance = cardNumber.getBalance().subtract(amount);
        cardNumber.setBalance(newBalance);

        List<ATM> allATMnames = atmRepository.findAll();
        int randomIndex = random.nextInt(allATMnames.size());
        ATM randomNameOfATM = allATMnames.get(randomIndex);

        Transaction transaction = new Transaction();
        transaction.setTransactionType("WITHDRAW_FROM_ATM");
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setSenderATM(randomNameOfATM);
        transaction.setRecipientCard(cardNumber);

        transactionRepo.save(transaction);

    }
}
