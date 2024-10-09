package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.dto.BankCardDTO;
import com.alex.d.springbootatm.service.KafkaProducerService;
import com.alex.d.springbootatm.model.BankCardModel;
import com.alex.d.springbootatm.repository.BankCardRepository;
import com.alex.d.springbootatm.service.ATMService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ManagerControllerTest {
    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    BankCardRepository bankCardRepository;

    @Mock
    ATMService atmService;

    @InjectMocks
    ManagerController managerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCards() {
        List<BankCardModel> cards = new ArrayList<>();
        cards.add(new BankCardModel(1L, "4000003813378680", "5356", BigDecimal.valueOf(300)));
        cards.add(new BankCardModel(2L, "4000007329214081", "3256", BigDecimal.valueOf(500)));

        when(bankCardRepository.findAll()).thenReturn(cards);

        ResponseEntity<List<BankCardModel>> response = managerController.getAllCards();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cards, response.getBody());

        verify(kafkaProducerService).sendMessage("atm-topic", "Retrieved " + cards.size() + " cards from the database");
    }

    @Test
    void testDeleteCard_Success() {
        String cardNumber = "4000007329214081";
        BankCardModel bankCard = new BankCardModel(1L, cardNumber, "5356", BigDecimal.valueOf(300));

        when(bankCardRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(bankCard));
        when(atmService.deleteCardByNumber(cardNumber)).thenReturn(Optional.of(bankCard));

        ResponseEntity<?> response = managerController.deleteCard(cardNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(kafkaProducerService).sendMessage("atm-topic", "Card with number " + cardNumber + " was deleted");
    }

    @Test
    void testDeleteCard_NotFound() {
        String cardNumber = "4000007329214081";

        when(bankCardRepository.findByCardNumber(cardNumber)).thenReturn(Optional.empty());

        ResponseEntity<?> response = managerController.deleteCard(cardNumber);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(kafkaProducerService).sendMessage("atm-topic", "Invalid credit card number " + cardNumber);
    }

    @Test
    void testCreateNewCard() {
        BankCardDTO newCard = new BankCardDTO("4000003813378680", "3256", BigDecimal.valueOf(0));

        when(atmService.createCard()).thenReturn(newCard);

        ResponseEntity<BankCardModel> response = managerController.createNewCard();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newCard, response.getBody());

        verify(kafkaProducerService).sendMessage("atm-topic", "New card created: " + newCard.getCardNumber() + " " + newCard.getPinCode());
    }
}
