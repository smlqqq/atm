package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.repository.CardATMRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class CardService {

    private final CardATMRepository cardATMRepository;


    public CardService(CardATMRepository cardATMRepository) {
        this.cardATMRepository = cardATMRepository;

    }

    public BankCard createCard() {
        BankCard card = new BankCard();
        card.setCardNumber(generateCreditCardNumber());
        card.setPinNumber(generatePinCode());
        card.setBalance(generateBalance());
        return cardATMRepository.save(card);
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
        return BigDecimal.valueOf(0); // Используйте вашу логику для генерации начального баланса
    }

//    public BankCard saveCard(BankCard cardATM){
//        return cardATMRepository.save(cardATM);
//    }


//    public void transferFunds(String senderCardNumber, String recipientCardNumber, BigDecimal amount) throws InsufficientFundsException {
//        // Получение данных о карте отправителя из базы данных
//        Optional<BankCard> senderCardOptional = cardATMRepository.findByCardNumber(senderCardNumber);
//        if (senderCardOptional.isEmpty()) {
//            throw new IllegalArgumentException("Sender card not found.");
//        }
//        BankCard senderCard = senderCardOptional.get();
//
//        // Получение данных о карте получателя из базы данных
//        Optional<BankCard> recipientCardOptional = cardATMRepository.findByCardNumber(recipientCardNumber);
//        if (recipientCardOptional.isEmpty()) {
//            throw new IllegalArgumentException("Recipient card not found.");
//        }
//        BankCard recipientCard = recipientCardOptional.get();
//
//        // Проверка наличия достаточных средств на карте отправителя
//        if (senderCard.getBalance().compareTo(amount) < 0) {
//            throw new InsufficientFundsException("Insufficient funds on sender's card.");
//        }
//
//        // Выполнение перевода средств
//        senderCard.setBalance(senderCard.getBalance().subtract(amount));
//        recipientCard.setBalance(recipientCard.getBalance().add(amount));
//
//        // Сохранение обновленных данных карт в базу данных
//        cardATMRepository.save(senderCard);
//        cardATMRepository.save(recipientCard);
//    }

//    public List<Optional<BankCard>> findCardsByBalanceOrderByCardNumber(BigDecimal balance) {
//        return cardATMRepository.findByBalanceOrderByCardNumber(balance);
//    }

    @Transactional(readOnly = true)
    public Optional<BankCard> findByCardNumber(String cardNumber) {
        return cardATMRepository.findByCardNumber(cardNumber);
    }


    @Transactional
    public BankCard saveCard(BankCard bankCard) {
       return cardATMRepository.save(bankCard);
    }

}
