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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BankCardServiceImpl implements BankCardService {

    private final CardRepository cardRepository;
    private final KafkaProducerService kafkaProducerService;
    private final BankCardGenerationService bankCardGenerationService;

    public BankCardServiceImpl(CardRepository cardRepository, KafkaProducerService kafkaProducerService, BankCardGenerationService bankCardGenerationService) {
        this.cardRepository = cardRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.bankCardGenerationService = bankCardGenerationService;
    }

    @Override
    @Transactional
    public List<BankCardDto> fetchAllBankCardsData() {
        return cardRepository.findAll().stream()
                .map(card -> BankCardDto.builder()
                        .cardNumber(card.getCardNumber())
//                        .pin(card.getPinNumber())
                        .balance(card.getBalance())
                        .build()
                )
                .toList();
    }

    @Override
    public BankCard fetchCardFromDb(String card) {
        return cardRepository.findByCardNumber(card)
                .orElseThrow(() -> new CardNotFoundException("Card not found: " + card));
    }


    @Override
    @Transactional
    public BankCardDto deleteBankCardByNumber(String cardNumber) {

        Optional<BankCard> optCard = Optional.ofNullable(fetchCardFromDb(cardNumber));

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
    public BankCardDto createBankCard() {

//        String pinCode = bankCardGenerationService.generatePinCode();

        char[] password = bankCardGenerationService.pinCodeGenerator();

        BankCard bankCard = bankCardGenerationService.buildCardModel(Arrays.toString(password));
        BankCardDto saveBankCardDTO = saveBankCardToDB(bankCard);

        log.info("Card successfully created {} pin {}", saveBankCardDTO.cardNumber(), password);
        Arrays.fill(password, '\0');

        log.info("Card created and saved into db {}", saveBankCardDTO.cardNumber());

        kafkaProducerService.setKafkaProducerServiceMessage(
                BankCardDto.builder()
                        .cardNumber(saveBankCardDTO.cardNumber())
                        .balance(saveBankCardDTO.balance())
                        .build(),
                KafkaTopic.KAFKA_MANAGER_TOPIC.getTopicName());

        return BankCardDto.builder()
                .cardNumber(saveBankCardDTO.cardNumber())
//                .pin(pinCode)
                .balance(saveBankCardDTO.balance())
                .build();
    }


//    @Override
//    @Transactional
//    public BankCardDto saveCardToDB(BankCard card) {
//        BankCard savedCard = cardRepository.save(card);
//        return BankCardDto.builder()
//                .cardNumber(savedCard.getCardNumber())
//                .pin(savedCard.getPinNumber())
//                .balance(savedCard.getBalance())
//                .build();
//    }

    @Override
    @Transactional
    public BankCardDto saveBankCardToDB(BankCard card) {
        BankCard savedCard = cardRepository.save(card);
        return BankCardDto.builder()
                .cardNumber(savedCard.getCardNumber())
                .balance(savedCard.getBalance())
                .build();
    }

}
