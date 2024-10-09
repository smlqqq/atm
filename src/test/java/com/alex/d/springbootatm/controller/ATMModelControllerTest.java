package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.model.BankCardModel;
import com.alex.d.springbootatm.repository.BankCardRepository;
import com.alex.d.springbootatm.response.BalanceResponse;
import com.alex.d.springbootatm.response.DepositResponse;
import com.alex.d.springbootatm.response.WithdrawResponse;
import com.alex.d.springbootatm.service.ATMService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ATMModelControllerTest {


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
    void testGetBalance() {
        String cardNumber = "4000007329214081";
        BankCardModel bankCardModel = new BankCardModel(1L, cardNumber, "5356", BigDecimal.valueOf(1000));

        when(bankCardRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(bankCardModel));
        when(atmService.checkBalanceByCardNumber(cardNumber)).thenReturn(BigDecimal.valueOf(1000));

        ResponseEntity<BalanceResponse> response = atmController.getBalance(cardNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        assertEquals(cardNumber, response.getBody().getCardNumber());
        assertEquals(BigDecimal.valueOf(1000), response.getBody().getBalance());
    }

    @Test
    void testDepositCash() {
        String cardNumber = "4000007329214081";
        BigDecimal amount = BigDecimal.valueOf(500);
        BankCardModel recipientCard = new BankCardModel(1L, cardNumber, "5356", BigDecimal.valueOf(0));

        when(bankCardRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(recipientCard));

        ResponseEntity<DepositResponse> response = atmController.depositCash(cardNumber, amount);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(cardNumber, response.getBody().getCardNumber());
        assertEquals(BigDecimal.valueOf(500), response.getBody().getBalance());

        verify(bankCardRepository, times(1)).findByCardNumber(cardNumber);
        verify(atmService, times(1)).depositCashFromATM(Optional.of(recipientCard), amount);
    }

    @Test
    void testWithdraw() {
        String cardNumber = "4000007329214081";
        BigDecimal amount = BigDecimal.valueOf(500);
        BankCardModel card = new BankCardModel(1L, cardNumber, "5356", BigDecimal.valueOf(1000));

        when(bankCardRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(card));

        ResponseEntity<WithdrawResponse> response = atmController.withdraw(cardNumber, amount);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(cardNumber, response.getBody().getCardNumber());
        assertEquals(amount.toString(), response.getBody().getWithdraw());
        assertEquals(BigDecimal.valueOf(500), response.getBody().getBalance());

        verify(bankCardRepository, times(1)).findByCardNumber(cardNumber);
        verify(atmService, times(1)).withdrawFromATM(Optional.of(card), amount);
    }

}