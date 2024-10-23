package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.dto.BankCardDTO;
import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.kafka.KafkaTopic;
import com.alex.d.springbootatm.model.BankCardModel;
import com.alex.d.springbootatm.repository.BankCardRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ManagerServiceimpl implements ManagerService{

    @Autowired
    private BankCardRepository bankCardRepository;
    @Autowired
    private AtmServiceImpl atmServiceimpl;

    @Override
    public Optional<List<BankCardModel>> getAllCards() {
        List<BankCardModel> cards = bankCardRepository.findAll();
        return cards.isEmpty() ? Optional.empty() : Optional.of(cards);
    }

    @Override
    @Transactional
    public BankCardModel deleteCardByNumber(String cardNumber) {

        Optional<BankCardModel> optCard = bankCardRepository.findByCardNumber(cardNumber);

        if (optCard.isPresent()) {
            bankCardRepository.delete(optCard.get());
            return optCard.get();
        } else {
            log.error("Card {} not exist", cardNumber);
            throw new CardNotFoundException("Card not found with number: " + cardNumber);
        }
    }

    @Override
    public BankCardDTO createCard() {

        BankCardModel card = atmServiceimpl.saveCreatedCardToDB();

        BankCardDTO bankCardDTO = BankCardDTO.builder()
                .cardNumber(card.getCardNumber())
                .pinCode(atmServiceimpl.generatePinCode())
                .build();

        log.info("Card and pin code info {} pin code {}", bankCardDTO.getCardNumber(), bankCardDTO.getPinCode());
        atmServiceimpl.setKafkaProducerService(bankCardDTO, KafkaTopic.KAFKA_MANAGER_TOPIC);

        return bankCardDTO;
    }

}
