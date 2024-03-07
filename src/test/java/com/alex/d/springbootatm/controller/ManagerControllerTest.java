package com.alex.d.springbootatm.controller;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ManagerControllerTest {

    @Mock
    BankCardRepository bankCardRepository;

    @Mock
    ATMService atmService;

    @InjectMocks
    ManagerController managerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
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
    }

//    @Test
//    void testDeleteCard() throws CardNotFoundException {
//        // Arrange
//        String cardNumber = "4377115590721505";
//        BankCard bankCard = new BankCard(1L, cardNumber, "5356", BigDecimal.valueOf(300));
//        when(atmService.deleteCardByNumber(cardNumber)).thenReturn(null);
//        ResponseEntity response = managerController.deleteCard(cardNumber);
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
//        assertNotNull(errorResponse);
//        assertEquals("Card not found", errorResponse.getError());
//    }

    @Test
    void testCreateNewCard() {
        BankCardModel newCard = new BankCardModel(1L, "4000003813378680", "3256", BigDecimal.valueOf(500));
        when(atmService.createCard()).thenReturn(newCard);
        ResponseEntity<BankCardModel> response = managerController.createNewCard(newCard);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newCard, response.getBody());
    }
}
