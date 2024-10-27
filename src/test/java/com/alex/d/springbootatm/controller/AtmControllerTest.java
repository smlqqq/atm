package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.response.BalanceResponse;
import com.alex.d.springbootatm.response.TransactionResponse;
import com.alex.d.springbootatm.service.AtmService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtmControllerTest {

    @Mock
    AtmService atmService;
    @InjectMocks
    AtmController atmController;

    @Test
    void getBalance() {
        String cardNumber = "4000007329214081";
        BigDecimal balance = BigDecimal.valueOf(1000);
        BalanceResponse expectedResponse = new BalanceResponse(cardNumber, balance);

        when(atmService.checkBalanceByCardNumber(cardNumber)).thenReturn(expectedResponse);

        ResponseEntity<BalanceResponse> response = atmController.getBalance(cardNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse.getCardNumber(), response.getBody().getCardNumber());
        assertEquals(expectedResponse.getBalance(), response.getBody().getBalance());
    }

    @Test
    void deposit() {
        String cardNumber = "4000007329214081";
        BigDecimal amount = BigDecimal.valueOf(500);
        TransactionResponse transactionResponse = new TransactionResponse(cardNumber, amount);

        when(atmService.updateAccountBalance(cardNumber, amount, true)).thenReturn(transactionResponse);

        ResponseEntity<?> response = atmController.deposit(cardNumber, amount);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(transactionResponse.getCardNumber(), ((TransactionResponse) response.getBody()).getCardNumber());
        assertEquals(transactionResponse.getBalance(), ((TransactionResponse) response.getBody()).getBalance());

    }

    @Test
    void withdraw() {

        String cardNumber = "4000007329214081";
        BigDecimal amount = BigDecimal.valueOf(200);
        BigDecimal cardBalance = BigDecimal.valueOf(1000);

        BalanceResponse balanceResponse = new BalanceResponse(cardNumber, cardBalance);
        TransactionResponse transactionResponse = new TransactionResponse(cardNumber, amount);

        when(atmService.checkBalanceByCardNumber(cardNumber)).thenReturn(balanceResponse);
        when(atmService.updateAccountBalance(cardNumber, amount, false)).thenReturn(transactionResponse);

        ResponseEntity<?> response = atmController.withdraw(cardNumber, amount);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(transactionResponse.getCardNumber(), ((TransactionResponse) response.getBody()).getCardNumber());

    }
}