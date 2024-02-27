package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.model.ATM;
import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.model.Transactions;
import com.alex.d.springbootatm.repository.ATMRepository;
import com.alex.d.springbootatm.repository.BankCardRepository;
import com.alex.d.springbootatm.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class ATMService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private BankCardRepository bankCardRepository;
    @Autowired
    private ATMRepository atmRepository;

    private final Random random = new Random();


    public ATMService(ATMRepository atmRepositoryMock, TransactionRepository transactionRepositoryMock) {
        this.atmRepository = atmRepositoryMock;
        this.transactionRepository = transactionRepositoryMock;
    }

    @Transactional
    public void sendTransaction(BankCard senderCard, BankCard recipientCard, BigDecimal amount) throws CardNotFoundException {
        if (senderCard != null && recipientCard != null) {
            // Create a new transaction
            Transactions transactions = new Transactions();
            transactions.setTransactionType("SEND");
            transactions.setAmount(amount);
            transactions.setTimestamp(LocalDateTime.now());
            transactions.setSenderCard(senderCard);
            transactions.setRecipientCard(recipientCard);
            transactionRepository.save(transactions);
            // Update sender's balance
            BigDecimal newSenderBalance = senderCard.getBalance().subtract(amount);
            senderCard.setBalance(newSenderBalance);
            // Update recipient's balance
            BigDecimal newRecipientBalance = recipientCard.getBalance().add(amount);
            recipientCard.setBalance(newRecipientBalance);
            // Save updated sender and recipient cards
            bankCardRepository.save(senderCard);
            bankCardRepository.save(recipientCard);
        } else {
            throw new CardNotFoundException("Sender card or recipient card not found");
        }
    }

    @Transactional
    public void depositCashFromATM(BankCard card, BigDecimal amount) throws CardNotFoundException {
        if (card == null) {
            throw new CardNotFoundException("Card not found.");
        }
        // Increase balance
        BigDecimal newBalance = card.getBalance().add(amount);
        card.setBalance(newBalance);
        // Create a new transaction
        Transactions transactions = new Transactions();
        transactions.setTransactionType("WITHDRAW_FROM_ATM");
        transactions.setAmount(amount);
        transactions.setTimestamp(LocalDateTime.now());
        transactions.setSenderATM(returnAtmName());
        transactions.setRecipientCard(card);
        transactionRepository.save(transactions);
    }


    @Transactional
    public void withdrawFromATM(BankCard card, BigDecimal amount) throws CardNotFoundException {
        if (card == null) {
            throw new CardNotFoundException("Card not found.");
        }
        // Decrease balance
        BigDecimal newBalance = card.getBalance().subtract(amount);
        card.setBalance(newBalance);
        // Create a new transaction
        Transactions transactions = new Transactions();
        transactions.setTransactionType("WITHDRAW_FROM_ATM");
        transactions.setAmount(amount);
        transactions.setTimestamp(LocalDateTime.now());
        transactions.setSenderATM(returnAtmName());
        transactions.setRecipientCard(card);
        transactionRepository.save(transactions);
    }

    public BankCard createCard() {
        BankCard card = new BankCard();
        card.setCardNumber(generateCreditCardNumber());
        card.setPinNumber(generatePinCode());
        card.setBalance(generateBalance());
        return bankCardRepository.save(card);
    }

    private String generatePinCode() {
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }

    public String generateCreditCardNumber() {
        StringBuilder sb = new StringBuilder("4"); // Начинаем с 4, как у Visa
        for (int i = 1; i < 15; i++) {
            sb.append((int) (Math.random() * 10));
        }

        String prefix = sb.toString();
        int checksum = LuhnsAlgorithm.calculateLuhnChecksum(prefix);
        sb.append(checksum);

        return sb.toString();
    }

    public ATM returnAtmName() {
        List<ATM> allAtmNames = atmRepository.findAll();
        int randomIndex = random.nextInt(allAtmNames.size());
        return allAtmNames.get(randomIndex);
    }

    public BigDecimal generateBalance() {
        return BigDecimal.valueOf(0);
    }

    @Transactional
    public BigDecimal checkBalance(String cardNumber) throws CardNotFoundException {
        BankCard card = bankCardRepository.findByCardNumber(cardNumber);
        if (card == null) {
            throw new CardNotFoundException("Card not found");
        }
        return card.getBalance();
    }

    @Transactional
    public BankCard deleteCardByNumber(String cardNumber) throws CardNotFoundException {
        BankCard card = bankCardRepository.findByCardNumber(cardNumber);
        if (card != null) {
            bankCardRepository.deleteByCardNumber(cardNumber);
        }
        return card;
    }



}
