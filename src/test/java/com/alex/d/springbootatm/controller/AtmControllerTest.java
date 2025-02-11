package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.model.response.BalanceResponse;
import com.alex.d.springbootatm.model.response.DepositeResponse;
import com.alex.d.springbootatm.model.response.WithdrawResponse;
import com.alex.d.springbootatm.service.atm.AtmService;
import com.alex.d.springbootatm.service.atm.TransactionType;
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

        ResponseEntity<BalanceResponse> response = atmController.balance(cardNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse.getCardNumber(), response.getBody().getCardNumber());
        assertEquals(expectedResponse.getBalance(), response.getBody().getBalance());
    }

    @Test
    void deposit() {
        String cardNumber = "4000007329214081";
        BigDecimal amount = BigDecimal.valueOf(500);
        DepositeResponse transactionResponse = new DepositeResponse(cardNumber, amount.toPlainString());

        when(atmService.processAtmTransaction(cardNumber, amount, String.valueOf(TransactionType.DEPOSIT))).thenReturn(transactionResponse);

        ResponseEntity<?> response = atmController.deposit(cardNumber, amount);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(transactionResponse.getCardNumber(), ((DepositeResponse) response.getBody()).getCardNumber());


    }

    @Test
    void withdraw() {
        String cardNumber = "4000007329214081";
        BigDecimal amount = BigDecimal.valueOf(200);

        WithdrawResponse transactionResponse = new WithdrawResponse(cardNumber, amount.toPlainString());

        when(atmService.processAtmTransaction(cardNumber, amount, String.valueOf(TransactionType.WITHDRAW))).thenReturn(transactionResponse);

        ResponseEntity<?> response = atmController.withdraw(cardNumber, amount);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(transactionResponse.getCardNumber(), ((WithdrawResponse) response.getBody()).getCardNumber());

    }
}