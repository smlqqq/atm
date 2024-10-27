package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.response.BalanceResponse;
import com.alex.d.springbootatm.response.TransferResponse;
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

        TransferResponse transferResponse = new TransferResponse(
                senderCardNumber,
                recipientCardNumber,
                amount,
                senderBalance.subtract(amount),
                recipientBalanceAfterTransfer
        );

        when(atmService.checkBalanceByCardNumber(senderCardNumber)).thenReturn(new BalanceResponse(senderCardNumber, senderBalance));
        when(atmService.transferBetweenCards(senderCardNumber, recipientCardNumber, amount)).thenReturn(transferResponse);

        ResponseEntity<?> response = transactionController.transferFundsToAnotherCard(senderCardNumber, recipientCardNumber, amount);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        TransferResponse actualResponse = (TransferResponse) response.getBody();

        assertEquals(transferResponse.getSenderCardNumber(), actualResponse.getSenderCardNumber());
        assertEquals(transferResponse.getRecipientCardNumber(), actualResponse.getRecipientCardNumber());
        assertEquals(transferResponse.getTransferred_funds(), actualResponse.getTransferred_funds());
        assertEquals(transferResponse.getSender_balance(), actualResponse.getSender_balance());
        assertEquals(transferResponse.getRecipient_balance(), actualResponse.getRecipient_balance());
    }
}
