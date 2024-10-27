package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.dto.CardDto;
import com.alex.d.springbootatm.model.CardModel;
import com.alex.d.springbootatm.repository.CardRepository;
import com.alex.d.springbootatm.service.ManagerService;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ManagerControllerTest {

    @Mock
    CardRepository bankCardRepository;

    @Mock
    ManagerService managerService;

    @InjectMocks
    ManagerController managerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllBankCards() {
        List<CardModel> cards = new ArrayList<>();
        cards.add(new CardModel(1L, "4000003813378680", "5356", BigDecimal.valueOf(300)));
        cards.add(new CardModel(2L, "4000007329214081", "3256", BigDecimal.valueOf(500)));

        when(managerService.getAllCards()).thenReturn(cards);
        List<CardModel> retrievedCards = managerService.getAllCards();

        assertNotNull(retrievedCards);
        assertEquals(2, retrievedCards.size());
        assertEquals("4000003813378680", retrievedCards.get(0).getCardNumber());
        assertEquals("4000007329214081", retrievedCards.get(1).getCardNumber());
    }

    @Test
    void deleteCard() {
        String cardNumber = "4000007329214081";
        CardModel bankCard = new CardModel(1L, cardNumber, "5356", BigDecimal.valueOf(300));

        when(bankCardRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(bankCard));
        when(managerService.deleteCardByNumber(cardNumber)).thenReturn(bankCard);

        ResponseEntity<?> response = managerController.deleteCard(cardNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createNewCard() {  CardDto newCard = new CardDto("4000003813378680", "3256", BigDecimal.valueOf(0));

        when(managerService.createCard()).thenReturn(newCard);

        ResponseEntity<CardDto> response = managerController.createNewCard();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newCard, response.getBody());

    }
}
