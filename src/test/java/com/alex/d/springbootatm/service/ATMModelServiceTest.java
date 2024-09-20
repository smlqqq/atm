package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.dto.BankCardDTO;
import com.alex.d.springbootatm.model.ATMModel;
import com.alex.d.springbootatm.model.BankCardModel;
import com.alex.d.springbootatm.repository.ATMRepository;
import com.alex.d.springbootatm.repository.BankCardRepository;
import com.alex.d.springbootatm.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ATMModelServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankCardRepository bankCardRepository;

    @Mock
    private ATMRepository atmRepository;

    @Mock
    private ATMService atmService;

    @Test
    public void testCheckBalance() {
        String cardNumber = "1234567890123456";
        BankCardModel bankCardModel = new BankCardModel();
        bankCardModel.setCardNumber(cardNumber);
        bankCardModel.setBalance(BigDecimal.valueOf(1000));
        when(bankCardRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of((bankCardModel)));
        BigDecimal balance = atmService.checkBalance(cardNumber);
        assertEquals(BigDecimal.valueOf(1000), balance);
    }

    @Test
    public void testDeleteCardByNumber() {
        String cardNumber = "1234567890123456";
        BankCardModel bankCardModel = new BankCardModel();
        bankCardModel.setCardNumber(cardNumber);
        when(bankCardRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of((bankCardModel)));
        Optional<BankCardModel> deletedCard = atmService.deleteCardByNumber(cardNumber);
        assertEquals((bankCardModel), deletedCard);
    }

    @Test
    public void testSendTransaction() {
        BankCardModel senderCard = new BankCardModel();
        senderCard.setBalance(BigDecimal.valueOf(1000));
        BankCardModel recipientCard = new BankCardModel();
        recipientCard.setBalance(BigDecimal.valueOf(500));
        BigDecimal amount = BigDecimal.valueOf(200);
        atmService.sendTransaction(Optional.of((senderCard)), Optional.of((recipientCard)), amount);
        verify(bankCardRepository, times(1)).save(senderCard);
        verify(bankCardRepository, times(1)).save(recipientCard);
    }

    @Test
    void testDepositCashFromATM() {
        BankCardModel card = new BankCardModel(1L, "1234567890123456", "1111", BigDecimal.valueOf(500));
        BigDecimal amount = BigDecimal.valueOf(200);
        List<ATMModel> allATMModels = new ArrayList<>();
        allATMModels.add(new ATMModel(1L, "ATM1", "null"));
        allATMModels.add(new ATMModel(2L, "ATM2", "null"));
        when(atmRepository.findAll()).thenReturn(allATMModels);
        atmService.depositCashFromATM(Optional.of(card), amount);
        assertEquals(BigDecimal.valueOf(700), card.getBalance());
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void testWithdrawFromATM() {
        BankCardModel card = new BankCardModel(1L, "1234567890123456", "1111", BigDecimal.valueOf(500));
        BigDecimal amount = BigDecimal.valueOf(200);
        List<ATMModel> allATMModels = new ArrayList<>();
        allATMModels.add(new ATMModel(1L, "ATM1", "null"));
        when(atmRepository.findAll()).thenReturn(allATMModels);
        atmService.withdrawFromATM(Optional.of(card), amount);
        assertEquals(BigDecimal.valueOf(300), card.getBalance());
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void testCreateCard() {
        BankCardModel newCard = new BankCardModel(1L, "1234567890123456", "1111", BigDecimal.valueOf(0));
        when(bankCardRepository.save(any())).thenReturn(newCard);
        BankCardDTO createdCard = atmService.createCard();
        assertNotNull(createdCard);
        assertEquals("1234567890123456", createdCard.getCardNumber());
        assertEquals("1111", createdCard.getPinCode());
        assertEquals(BigDecimal.valueOf(0), createdCard.getBalance());
    }
}
