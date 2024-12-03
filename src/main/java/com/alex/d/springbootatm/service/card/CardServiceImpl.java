package com.alex.d.springbootatm.service.card;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.messaging.KafkaProducerService;
import com.alex.d.springbootatm.messaging.KafkaTopic;
import com.alex.d.springbootatm.model.CardModel;
import com.alex.d.springbootatm.model.dto.CardDto;
import com.alex.d.springbootatm.repository.CardRepository;
import com.alex.d.springbootatm.service.atm.AtmService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CardServiceImpl implements CardService {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private KafkaProducerService kafkaProducerService;
    @Autowired
    private AtmService atmService;
    @Autowired
    private CardGenerationService cardGenerationService;

    @Override
    @Transactional
    public List<CardDto> getAllCards() {
        return cardRepository.findAll().stream()
                .map(card -> CardDto.builder()
                        .cardNumber(card.getCardNumber())
                        .pin(card.getPinNumber())
                        .balance(card.getBalance())
                        .build()
                )
                .toList();
    }


    @Override
    @Transactional
    public CardDto deleteCardByNumber(String cardNumber) {

        Optional<CardModel> optCard = Optional.ofNullable(atmService.fetchCardFromDb(cardNumber));

        if (optCard.isPresent()) {
            cardRepository.delete(optCard.get());
            log.info("Card successfully deleted {}", cardNumber);
            return CardDto.builder()
                    .cardNumber(optCard.get().getCardNumber())
                    .pin(optCard.get().getPinNumber())
                    .balance(optCard.get().getBalance())
                    .build();
        } else {
            log.error("Card {} not exist", cardNumber);
            throw new CardNotFoundException("Card not found with number: " + cardNumber);
        }
    }


    @Override
    public CardDto createAndSaveCard() {

        String pinCode = cardGenerationService.generatePinCode();

        CardModel cardModel = cardGenerationService.buildCardModel(pinCode);

        CardDto savedCard = saveCreatedCardToDB(cardModel);

        log.info("Card created and saved into db {} hashed pin code {}", savedCard.getCardNumber(), savedCard.getPin());

        kafkaProducerService.setKafkaProducerServiceMessage(
                CardDto.builder()
                        .cardNumber(savedCard.getCardNumber())
                        .pin("SECRET")
                        .balance(savedCard.getBalance())
                        .build(),
                KafkaTopic.KAFKA_MANAGER_TOPIC.getTopicName());

        return CardDto.builder()
                .cardNumber(savedCard.getCardNumber())
                .pin(pinCode)
                .balance(savedCard.getBalance())
                .build();
    }


    @Override
    @Transactional
    public CardDto saveCreatedCardToDB(CardModel card) {
        CardModel savedCard = cardRepository.save(card);
        return CardDto.builder()
                .cardNumber(savedCard.getCardNumber())
                .pin(savedCard.getPinNumber())
                .balance(savedCard.getBalance())
                .build();
    }

}
