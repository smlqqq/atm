package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.dto.CardDto;
import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.kafka.KafkaProducerService;
import com.alex.d.springbootatm.kafka.KafkaTopic;
import com.alex.d.springbootatm.model.CardModel;
import com.alex.d.springbootatm.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private KafkaProducerService kafkaProducerService;
    @Autowired
    private AtmService atmService;

    @Override
    public List<CardModel> getAllCards() {
        return cardRepository.findAll();
    }

    @Override
    @Transactional
    public CardModel deleteCardByNumber(String cardNumber) {

        Optional<CardModel> optCard = Optional.ofNullable(atmService.fetchCardModel(cardNumber));

        if (optCard.isPresent()) {
            cardRepository.delete(optCard.get());
            return optCard.get();
        } else {
            log.error("Card {} not exist", cardNumber);
            throw new CardNotFoundException("Card not found with number: " + cardNumber);
        }
    }

    @Override
    public CardDto createCard() {

        CardModel card = saveCreatedCardToDB();
        return CardDto.builder()
                .cardNumber(card.getCardNumber())
                .pinCode(generatePinCode())
                .balance(generateBalance())
                .build();
    }

    @Override
    @Transactional
    public CardModel saveCreatedCardToDB() {

        CardModel cardModel = CardModel.builder()
                .cardNumber(generateCreditCardNumber())
                .pinNumber(hashPinCode(generatePinCode()))
                .balance(generateBalance())
                .build();

        log.info("Card saved into db {} pin code {}", cardModel.getCardNumber(), cardModel.getPinNumber());

        kafkaProducerService.setKafkaProducerServiceMessage(
                CardDto.builder()
                        .cardNumber(cardModel.getCardNumber())
                        .pinCode(cardModel.getPinNumber())
                        .balance(cardModel.getBalance())
                        .build(),
                KafkaTopic.KAFKA_MANAGER_TOPIC);

        return cardRepository.save(cardModel);
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
    public String generatePinCode() {
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }

    @Override
    public String hashPinCode(String pinCode) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(pinCode);
    }


}
