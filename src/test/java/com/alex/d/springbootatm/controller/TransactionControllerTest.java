package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.repository.BankCardRepository;
import com.alex.d.springbootatm.service.ATMService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private BankCardRepository bankCardRepository;

    @Mock
    private ATMService atmService;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    void testTransferFunds() throws CardNotFoundException {
        String senderCardNumber = "1234567890123456";
        String recipientCardNumber = "9876543210987654";
        BigDecimal amount = BigDecimal.valueOf(500);
        BankCard senderCard = new BankCard(1L, senderCardNumber, "1111", BigDecimal.valueOf(1000));
        BankCard recipientCard = new BankCard(2L, recipientCardNumber, "2222", BigDecimal.valueOf(0));
        when(bankCardRepository.findByCardNumber(senderCardNumber)).thenReturn(senderCard);
        when(bankCardRepository.findByCardNumber(recipientCardNumber)).thenReturn(recipientCard);
        doNothing().when(atmService).sendTransaction(senderCard, recipientCard, amount);
        ResponseEntity<String> response = transactionController.transferFunds(senderCardNumber, recipientCardNumber, amount);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Transactions sent successfully.", response.getBody());
        verify(bankCardRepository, times(1)).findByCardNumber(senderCardNumber);
        verify(bankCardRepository, times(1)).findByCardNumber(recipientCardNumber);
        verify(atmService, times(1)).sendTransaction(senderCard, recipientCard, amount);
    }
}
