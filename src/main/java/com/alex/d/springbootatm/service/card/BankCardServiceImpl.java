package com.alex.d.springbootatm.service.card;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.messaging.KafkaProducerService;
import com.alex.d.springbootatm.messaging.KafkaTopic;
import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.dto.BankCardDto;
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
public class BankCardServiceImpl implements BankCardService {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private KafkaProducerService kafkaProducerService;
    @Autowired
    private AtmService atmService;
    @Autowired
    private BankCardGenerationService bankCardGenerationService;

    @Override
    @Transactional
    public List<BankCardDto> getAllCards() {
        return cardRepository.findAll().stream()
                .map(card -> BankCardDto.builder()
                        .cardNumber(card.getCardNumber())
                        .pin(card.getPinNumber())
                        .balance(card.getBalance())
                        .build()
                )
                .toList();
    }


    @Override
    @Transactional
    public BankCardDto deleteCardByNumber(String cardNumber) {

        Optional<BankCard> optCard = Optional.ofNullable(atmService.fetchCardFromDb(cardNumber));

        if (optCard.isPresent()) {
            cardRepository.delete(optCard.get());
            log.info("Card successfully deleted {}", cardNumber);
            return BankCardDto.builder()
                    .cardNumber(optCard.get().getCardNumber())
                    .balance(optCard.get().getBalance())
                    .build();
        } else {
            log.error("Card {} not exist", cardNumber);
            throw new CardNotFoundException("Card not found with number: " + cardNumber);
        }
    }


    @Override
    public BankCardDto createCard() {

        String pinCode = bankCardGenerationService.generatePinCode();

        BankCard bankCard = bankCardGenerationService.buildCardModel(pinCode);

        BankCardDto savedCard = saveCardToDB(bankCard);

        log.info("Card created and saved into db {} hashed pin code {}", savedCard.getCardNumber(), savedCard.getPin());

        kafkaProducerService.setKafkaProducerServiceMessage(
                BankCardDto.builder()
                        .cardNumber(savedCard.getCardNumber())
                        .pin("***")
                        .balance(savedCard.getBalance())
                        .build(),
                KafkaTopic.KAFKA_MANAGER_TOPIC.getTopicName());

        return BankCardDto.builder()
                .cardNumber(savedCard.getCardNumber())
                .pin(pinCode)
                .balance(savedCard.getBalance())
                .build();
    }


    @Override
    @Transactional
    public BankCardDto saveCardToDB(BankCard card) {
        BankCard savedCard = cardRepository.save(card);
        return BankCardDto.builder()
                .cardNumber(savedCard.getCardNumber())
                .pin(savedCard.getPinNumber())
                .balance(savedCard.getBalance())
                .build();
    }

}
