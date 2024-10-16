package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.dto.BankCardDTO;
import com.alex.d.springbootatm.model.ATMModel;
import com.alex.d.springbootatm.model.BankCardModel;
import com.alex.d.springbootatm.model.TransactionModel;
import com.alex.d.springbootatm.repository.ATMRepository;
import com.alex.d.springbootatm.repository.BankCardRepository;
import com.alex.d.springbootatm.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class AtmServiceImpl implements ATMService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private BankCardRepository bankCardRepository;
    @Autowired
    private ATMRepository atmRepository;

    @Override
    @Transactional
    public void sendTransaction(Optional<BankCardModel> senderCard, Optional<BankCardModel> recipientCard, BigDecimal amount) {
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
        // Set balances after transaction in the transaction model
        transactionModel.setSenderBalanceAfter(newSenderBalance);
        transactionModel.setRecipientBalanceAfter(newRecipientBalance);
        // Save updated sender and recipient cards
        bankCardRepository.save(senderCard.get());
        bankCardRepository.save(recipientCard.get());

    }

    @Override
    @Transactional
    public void depositCashFromATM(Optional<BankCardModel> card, BigDecimal amount) {
        // Increase balance
        BigDecimal newBalance = card.get().getBalance().add(amount);
        card.get().setBalance(newBalance);
        // Create a new transaction
        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setTransactionType("DEPOSIT_FROM");
        transactionModel.setAmount(amount);
        transactionModel.setTimestamp(LocalDateTime.now());
        transactionModel.setSenderATMModel(returnAtmName());
        transactionModel.setRecipientCard(card.get());
        transactionModel.setRecipientBalanceAfter(newBalance);
        transactionRepository.save(transactionModel);

    }


    @Override
    @Transactional
    public void withdrawFromATM(Optional<BankCardModel> card, BigDecimal amount) {
        // Decrease balance
        BigDecimal newBalance = card.get().getBalance().subtract(amount);
        card.get().setBalance(newBalance);
        // Create a new transaction
        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setTransactionType("WITHDRAW_FROM");
        transactionModel.setAmount(amount);
        transactionModel.setTimestamp(LocalDateTime.now());
        transactionModel.setSenderATMModel(returnAtmName());
        transactionModel.setRecipientCard(card.get());
        transactionRepository.save(transactionModel);

    }

    @Override
    @Transactional
    public BankCardModel saveCreatedCardToDB() {
        BankCardModel card = new BankCardModel();
        card.setCardNumber(generateCreditCardNumber());
        card.setPinNumber(hashPinCode(generatePinCode()));
        log.info("Card saved into db {} pin code {}", card.getCardNumber(), card.getPinNumber());
        card.setBalance(generateBalance());
        return bankCardRepository.save(card);
    }

    @Override
    public BankCardDTO createCard() {
        BankCardDTO responseDto = new BankCardDTO();
        BankCardModel card = saveCreatedCardToDB();
        responseDto.setCardNumber(card.getCardNumber());
        responseDto.setPinCode(generatePinCode());
        log.info("Card and pincode info {} pin code {}", responseDto.getCardNumber(), responseDto.getPinCode());
        responseDto.setBalance(card.getBalance());
        return responseDto;
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
    public BigDecimal checkBalanceByCardNumber(String cardNumber) {
        Optional<BankCardModel> card = bankCardRepository.findByCardNumber(cardNumber);
        if (card.isPresent()) {
            return card.get().getBalance();
        } else
            throw new NoSuchElementException("Card not found with number: " + cardNumber);
    }

    @Override
    @Transactional
    public Optional<BankCardModel> deleteCardByNumber(String cardNumber) {
        Optional<BankCardModel> card = bankCardRepository.findByCardNumber(cardNumber);
        if (card.isPresent()) {
            bankCardRepository.delete(card.get());
            return card;
        } else {
            throw new EntityNotFoundException("Card not found " + cardNumber);
        }
    }

    @Override
    public ATMModel returnAtmName() {
        Random random = new Random();
        List<ATMModel> allATMModelNames = atmRepository.findAll();
        int randomIndex = random.nextInt(allATMModelNames.size());
        return allATMModelNames.get(randomIndex);
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

}
