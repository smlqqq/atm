package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.model.ATMModel;
import com.alex.d.springbootatm.model.BankCardModel;
import com.alex.d.springbootatm.model.TransactionModel;
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


    @Transactional
    public void sendTransaction(BankCardModel senderCard, BankCardModel recipientCard, BigDecimal amount) throws CardNotFoundException {
        if (senderCard != null && recipientCard != null) {
            // Create a new transaction
            TransactionModel transactionModel = new TransactionModel();
            transactionModel.setTransactionType("SEND");
            transactionModel.setAmount(amount);
            transactionModel.setTimestamp(LocalDateTime.now());
            transactionModel.setSenderCard(senderCard);
            transactionModel.setRecipientCard(recipientCard);
            transactionRepository.save(transactionModel);
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
    public void depositCashFromATM(BankCardModel card, BigDecimal amount) throws CardNotFoundException {
        if (card != null) {
            // Increase balance
            BigDecimal newBalance = card.getBalance().add(amount);
            card.setBalance(newBalance);
            // Create a new transaction
            TransactionModel transactionModel = new TransactionModel();
            transactionModel.setTransactionType("DEPOSIT_FROM_ATM");
            transactionModel.setAmount(amount);
            transactionModel.setTimestamp(LocalDateTime.now());
            transactionModel.setSenderATMModel(returnAtmName());
            transactionModel.setRecipientCard(card);
            transactionRepository.save(transactionModel);
        } else {
            throw new CardNotFoundException("Card not found.");
        }
    }


    @Transactional
    public void withdrawFromATM(BankCardModel card, BigDecimal amount) throws CardNotFoundException {
        if (card != null) {
            // Decrease balance
            BigDecimal newBalance = card.getBalance().subtract(amount);
            card.setBalance(newBalance);
            // Create a new transaction
            TransactionModel transactionModel = new TransactionModel();
            transactionModel.setTransactionType("WITHDRAW_FROM_ATM");
            transactionModel.setAmount(amount);
            transactionModel.setTimestamp(LocalDateTime.now());
            transactionModel.setSenderATMModel(returnAtmName());
            transactionModel.setRecipientCard(card);
            transactionRepository.save(transactionModel);
        }else {
            throw new CardNotFoundException("Card not found.");
        }
    }

    public BankCardModel createCard() {
        BankCardModel card = new BankCardModel();
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
        StringBuilder sb = new StringBuilder("400000");
        for (int i = 1; i < 10; i++) {
            sb.append((int) (Math.random() * 10));
        }
        String prefix = sb.toString();
        int checksum = LuhnsAlgorithm.calculateChecksum(prefix);
        sb.append(checksum);
        return sb.toString();
    }

    public BigDecimal generateBalance() {
        return BigDecimal.valueOf(0);
    }

    @Transactional
    public BigDecimal checkBalance(String cardNumber) throws CardNotFoundException {
        BankCardModel card = bankCardRepository.findByCardNumber(cardNumber);
        if (card == null) {
            throw new CardNotFoundException("Card not found");
        }
        return card.getBalance();
    }

    @Transactional
    public BankCardModel deleteCardByNumber(String cardNumber) throws CardNotFoundException {
        BankCardModel card = bankCardRepository.findByCardNumber(cardNumber);
        if (card != null) {
            bankCardRepository.deleteByCardNumber(cardNumber);
        }
        return card;
    }

    public ATMModel returnAtmName() {
        List<ATMModel> allATMModelNames = atmRepository.findAll();
        int randomIndex = random.nextInt(allATMModelNames.size());
        return allATMModelNames.get(randomIndex);
    }



}
