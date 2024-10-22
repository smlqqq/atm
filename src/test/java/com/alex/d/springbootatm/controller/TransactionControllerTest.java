//package com.alex.d.springbootatm.controller;
//
//import com.alex.d.springbootatm.model.BankCardModel;
//import com.alex.d.springbootatm.repository.BankCardRepository;
//import com.alex.d.springbootatm.response.TransferResponse;
//import com.alex.d.springbootatm.service.ATMService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TransactionControllerTest {
//
//    @Mock
//    BankCardRepository bankCardRepository;
//
//    @Mock
//    ATMService atmService;
//
//    @InjectMocks
//    TransactionController transactionController;
//
//    @Test
//    void testTransferFundsToAnotherCard() {
//        // Arrange
//        String senderCardNumber = "4000003813378680";
//        String recipientCardNumber = "4000007329214081";
//
//        BigDecimal amount = BigDecimal.valueOf(500);
//
//        BankCardModel senderCard = new BankCardModel(1L, senderCardNumber, "1111", BigDecimal.valueOf(1000));
//        BankCardModel recipientCard = new BankCardModel(2L, recipientCardNumber, "2222", BigDecimal.valueOf(0));
//
//        when(bankCardRepository.findByCardNumber(senderCardNumber)).thenReturn(Optional.of(senderCard));
//        when(bankCardRepository.findByCardNumber(recipientCardNumber)).thenReturn(Optional.of(recipientCard));
//
//        doNothing().when(atmService).sendTransaction(Optional.of(senderCard), Optional.of(recipientCard), amount);
//
//        ResponseEntity<TransferResponse> response = transactionController.transferFundsToAnotherCard(senderCardNumber, recipientCardNumber, amount);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(senderCardNumber, response.getBody().getSenderCardNumber());
//        assertEquals(recipientCardNumber, response.getBody().getRecipientCardNumber());
//        assertEquals(amount, response.getBody().getTransferred_funds());
//        assertEquals(amount, response.getBody().getRecipient_balance());
//
//        verify(bankCardRepository, times(1)).findByCardNumber(senderCardNumber);
//        verify(bankCardRepository, times(1)).findByCardNumber(recipientCardNumber);
//        verify(atmService, times(1)).sendTransaction(Optional.of(senderCard), Optional.of(recipientCard), amount);
//    }
//}
