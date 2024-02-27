package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.model.BankCard;
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
import static org.mockito.Mockito.*;

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
        List<BankCard> cards = new ArrayList<>();
        cards.add(new BankCard(1L,"4377115590721505","5356", BigDecimal.valueOf(300)));
        cards.add(new BankCard(1L,"4377115590721456","3256", BigDecimal.valueOf(500)));
        when(bankCardRepository.findAll()).thenReturn(cards);
        ResponseEntity<List<BankCard>> response = managerController.getAllCards();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cards, response.getBody());
    }

    @Test
    void testDeleteCard() throws CardNotFoundException {
        BankCard bankCard = new BankCard(1L,"4377115590721505","5356", BigDecimal.valueOf(300));
        when(atmService.deleteCardByNumber(String.valueOf(bankCard))).thenReturn(null);
        ResponseEntity<Void> response = managerController.deleteCard(String.valueOf(bankCard));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(atmService, times(1)).deleteCardByNumber(String.valueOf((bankCard)));
    }

    @Test
    void testCreateNewCard() {
        BankCard newCard = new BankCard(1L,"4377115590721456","3256", BigDecimal.valueOf(500));
        when(atmService.createCard()).thenReturn(newCard);
        ResponseEntity<BankCard> response = managerController.createNewCard(new BankCard());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newCard, response.getBody());
    }
}
