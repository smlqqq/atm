package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.model.ATM;
import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.model.Transactions;
import com.alex.d.springbootatm.repository.ATMRepository;
import com.alex.d.springbootatm.repository.BankCardRepository;
import com.alex.d.springbootatm.repository.TransactionRepository;
import com.alex.d.springbootatm.service.ATMService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ATMControllerTest {


    @Mock
    private BankCardRepository bankCardRepository;

    @Mock
    private ATMService atmService;

    @InjectMocks
    private ATMController atmController;


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

    @Test
    void testDepositCash() throws CardNotFoundException {
        String cardNum = "4377115590721505";
        BigDecimal amount = BigDecimal.valueOf(500);
        BankCard recipientCard = new BankCard(1L, cardNum, "5356", BigDecimal.valueOf(0));
        when(bankCardRepository.findByCardNumber(cardNum)).thenReturn(recipientCard);
        doNothing().when(atmService).depositCashFromATM(recipientCard, amount);
        ResponseEntity<String> response = atmController.depositCash(cardNum, amount);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Money successfully deposited.", response.getBody());
        verify(bankCardRepository, times(1)).findByCardNumber(cardNum);
        verify(atmService, times(1)).depositCashFromATM(recipientCard, amount);
    }

    @Test
    void testWithdraw() throws CardNotFoundException {
        String cardNumber = "4377115590721505";
        BigDecimal amount = BigDecimal.valueOf(500);
        BankCard card = new BankCard(1L, cardNumber, "5356", BigDecimal.valueOf(1000));
        when(bankCardRepository.findByCardNumber(cardNumber)).thenReturn(card);
        doNothing().when(atmService).withdrawFromATM(card, amount);
        ResponseEntity<String> response = atmController.withdraw(cardNumber, amount);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Money successfully withdrawn.", response.getBody());
        verify(bankCardRepository, times(1)).findByCardNumber(cardNumber);
        verify(atmService, times(1)).withdrawFromATM(card, amount);
    }


}