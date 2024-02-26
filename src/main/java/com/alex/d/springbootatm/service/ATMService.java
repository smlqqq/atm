package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.model.ATM;
import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.model.Transactions;
import com.alex.d.springbootatm.repository.ATMRepository;
import com.alex.d.springbootatm.repository.BankCardRepository;
import com.alex.d.springbootatm.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ATMService {

    private final TransactionRepository transactionRepository;
    private final BankCardRepository bankCardRepository;
    private final ATMRepository atmRepository;

    private final Random random = new Random();


    public ATMService(TransactionRepository transactionRepository, BankCardRepository bankCardRepository, ATMRepository atmRepository) {
        this.transactionRepository = transactionRepository;
        this.bankCardRepository = bankCardRepository;
        this.atmRepository = atmRepository;
    }

    @Transactional
    public void saveTransaction(Transactions transactions) {
        transactionRepository.save(transactions);
    }

    @Transactional
    public void sendTransaction(Optional<BankCard> optionalSenderCard, Optional<BankCard> optionalRecipientCard, BigDecimal amount) throws CardNotFoundException {
        if (optionalSenderCard.isPresent() && optionalRecipientCard.isPresent()) {
            BankCard senderCard = optionalSenderCard.get();
            BankCard recipientCard = optionalRecipientCard.get();
            // Create a new transactions
            Transactions transactions = new Transactions();
            transactions.setTransactionType("SEND");
            transactions.setAmount(amount);
            transactions.setTimestamp(LocalDateTime.now());
            transactions.setSenderCard(senderCard);
            transactions.setRecipientCard(recipientCard);
            saveTransaction(transactions);
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
    public void depositCashFromATM(Optional<BankCard> optionalRecipientCard, BigDecimal amount) throws CardNotFoundException {
        BankCard recipientCard = optionalRecipientCard.orElseThrow(() -> new CardNotFoundException("Recipient card not found."));
        // Decrease balance
        BigDecimal newBalance = recipientCard.getBalance().add(amount);
        recipientCard.setBalance(newBalance);
        List<ATM> allAtmNames = atmRepository.findAll();
        int randomIndex = random.nextInt(allAtmNames.size());
        ATM randomAtmName = allAtmNames.get(randomIndex);
        // Create and save transactions
        Transactions transactions = new Transactions();
        transactions.setTransactionType("DEPOSIT_FROM_ATM");
        transactions.setAmount(amount);
        transactions.setTimestamp(LocalDateTime.now());
        transactions.setSenderATM(randomAtmName);
        transactions.setRecipientCard(recipientCard);
        transactionRepository.save(transactions);
    }


    @Transactional
    public void withdrawFromATM(Optional<BankCard> card, BigDecimal amount) throws CardNotFoundException {
        BankCard cardNumber = card.orElseThrow(() -> new CardNotFoundException("Card not found."));
        BigDecimal newBalance = cardNumber.getBalance().subtract(amount);
        cardNumber.setBalance(newBalance);
        List<ATM> ATMName = atmRepository.findAll();
        int randomIndex = random.nextInt(ATMName.size());
        ATM randomNameOfATM = ATMName.get(randomIndex);
        Transactions transactions = new Transactions();
        transactions.setTransactionType("WITHDRAW_FROM_ATM");
        transactions.setAmount(amount);
        transactions.setTimestamp(LocalDateTime.now());
        transactions.setSenderATM(randomNameOfATM);
        transactions.setRecipientCard(cardNumber);
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

    public BigDecimal generateBalance() {
        return BigDecimal.valueOf(0);
    }

    @Transactional
    public BigDecimal checkBalance(String cardNumber) throws CardNotFoundException {
        Optional<BankCard> card = bankCardRepository.findByCardNumber(cardNumber);
        if (card.isEmpty()) {
            throw new CardNotFoundException("Card not found");
        }
        return card.get().getBalance();
    }

    @Transactional
    public void deleteCardByNumber(String cardNumber) throws CardNotFoundException {
        Optional<BankCard> cardOptional = bankCardRepository.findByCardNumber(cardNumber);
        if (cardOptional.isPresent()) {
            bankCardRepository.deleteByCardNumber(cardNumber);
        } else {
            throw new CardNotFoundException("Card with number " + cardNumber + " not found");
        }
    }

}
