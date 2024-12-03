package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.model.dto.response.BalanceResponse;
import com.alex.d.springbootatm.model.dto.response.TransactionResponse;
import com.alex.d.springbootatm.service.atm.AtmService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    AtmService atmService;

    @InjectMocks
    TransactionController transactionController;

    @Test
    void transferFundsToAnotherCard() {
        String senderCardNumber = "4000007329214081";
        String recipientCardNumber = "4000003813378680";
        BigDecimal amount = BigDecimal.valueOf(200);
        BigDecimal senderBalance = BigDecimal.valueOf(500);
        BigDecimal recipientBalanceAfterTransfer = BigDecimal.valueOf(1000);

        TransactionResponse transactionResponse = new TransactionResponse(
                senderCardNumber,
                recipientCardNumber,
                amount,
                senderBalance.subtract(amount),
                recipientBalanceAfterTransfer
        );

        when(atmService.checkBalanceByCardNumber(senderCardNumber)).thenReturn(new BalanceResponse(senderCardNumber, senderBalance));
        when(atmService.transferBetweenCards(senderCardNumber, recipientCardNumber, amount)).thenReturn(transactionResponse);

        ResponseEntity<?> response = transactionController.transferFundsToAnotherCard(senderCardNumber, recipientCardNumber, amount);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        TransactionResponse actualResponse = (TransactionResponse) response.getBody();

        assertEquals(transactionResponse.getSenderCardNumber(), actualResponse.getSenderCardNumber());
        assertEquals(transactionResponse.getRecipientCardNumber(), actualResponse.getRecipientCardNumber());
        assertEquals(transactionResponse.getTransferredFunds(), actualResponse.getTransferredFunds());
        assertEquals(transactionResponse.getSenderBalance(), actualResponse.getSenderBalance());
        assertEquals(transactionResponse.getRecipientBalance(), actualResponse.getRecipientBalance());
    }
}
