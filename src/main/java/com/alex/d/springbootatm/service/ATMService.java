package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.model.ATMModel;
import com.alex.d.springbootatm.model.BankCardModel;
import com.alex.d.springbootatm.model.TransactionModel;
import com.alex.d.springbootatm.repository.ATMRepository;
import com.alex.d.springbootatm.repository.BankCardRepository;
import com.alex.d.springbootatm.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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



    public void sendTransaction(Optional<BankCardModel> senderCard, Optional<BankCardModel> recipientCard, BigDecimal amount){
            // Create a new transaction
            TransactionModel transactionModel = new TransactionModel();
            transactionModel.setTransactionType("SEND");
            transactionModel.setAmount(amount);
            transactionModel.setTimestamp(LocalDateTime.now());
            transactionModel.setSenderCard(senderCard.get());
            transactionModel.setRecipientCard(recipientCard.get());
            transactionRepository.save(transactionModel);
            // Update sender's balance
            BigDecimal newSenderBalance = senderCard.get().getBalance().subtract(amount);
            senderCard.get().setBalance(newSenderBalance);
            // Update recipient's balance
            BigDecimal newRecipientBalance = recipientCard.get().getBalance().add(amount);
            recipientCard.get().setBalance(newRecipientBalance);
            // Save updated sender and recipient cards
            bankCardRepository.save(senderCard.get());
            bankCardRepository.save(recipientCard.get());

    }


    public void depositCashFromATM(Optional<BankCardModel> card, BigDecimal amount){
            // Increase balance
            BigDecimal newBalance = card.get().getBalance().add(amount);
            card.get().setBalance(newBalance);
            // Create a new transaction
            TransactionModel transactionModel = new TransactionModel();
            transactionModel.setTransactionType("DEPOSIT_FROM_ATM");
            transactionModel.setAmount(amount);
            transactionModel.setTimestamp(LocalDateTime.now());
            transactionModel.setSenderATMModel(returnAtmName());
            transactionModel.setRecipientCard(card.get());
            transactionRepository.save(transactionModel);

    }


    public void withdrawFromATM(Optional<BankCardModel> card, BigDecimal amount){
            // Decrease balance
            BigDecimal newBalance = card.get().getBalance().subtract(amount);
            card.get().setBalance(newBalance);
            // Create a new transaction
            TransactionModel transactionModel = new TransactionModel();
            transactionModel.setTransactionType("WITHDRAW_FROM_ATM");
            transactionModel.setAmount(amount);
            transactionModel.setTimestamp(LocalDateTime.now());
            transactionModel.setSenderATMModel(returnAtmName());
            transactionModel.setRecipientCard(card.get());
            transactionRepository.save(transactionModel);

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


    public BigDecimal checkBalance(String cardNumber) {
        Optional<BankCardModel> card = bankCardRepository.findByCardNumber(cardNumber);
        return card.get().getBalance();
    }


    public Optional<BankCardModel> deleteCardByNumber(String cardNumber){
        Optional<BankCardModel> card = bankCardRepository.findByCardNumber(cardNumber);
        bankCardRepository.delete(card.get());
        return card;
    }

    public ATMModel returnAtmName() {
        List<ATMModel> allATMModelNames = atmRepository.findAll();
        int randomIndex = random.nextInt(allATMModelNames.size());
        return allATMModelNames.get(randomIndex);
    }

    

}
