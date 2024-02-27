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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ATMControllerTest {


    @Mock
    BankCardRepository bankCardRepository;

    @Mock
    ATMService atmService;

    @InjectMocks
    ATMController atmController;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCardBalanceEndpoint() throws CardNotFoundException {
        BankCard card = new BankCard(1L, "4377115590721505", "5356", BigDecimal.valueOf(300));
        when(atmService.checkBalance(card.getCardNumber())).thenReturn(card.getBalance());
        ResponseEntity<BigDecimal> response = atmController.getBalance(Long.valueOf(card.getCardNumber()));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(card.getBalance(), response.getBody());
    }

//    @Test
//    void testDepositEndpoint() throws CardNotFoundException {
//
//        BankCard card = new BankCard(1L, "4377115590721505", "5356", BigDecimal.valueOf(0));
//        BigDecimal cash = BigDecimal.valueOf(500);
//        when(atmService.depositCashFromATM(card, cash));
//
//
//
//    }


    @Test
    void testWithdrawEndpoint() throws CardNotFoundException {

    }


}