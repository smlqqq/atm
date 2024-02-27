package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.model.ATM;
import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.model.Transactions;
import com.alex.d.springbootatm.repository.ATMRepository;
import com.alex.d.springbootatm.repository.BankCardRepository;
import com.alex.d.springbootatm.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ATMServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankCardRepository bankCardRepository;

    @Mock
    private ATMRepository atmRepository;

    @InjectMocks
    private ATMService atmService;

    @Test
    public void testCheckBalance() throws CardNotFoundException {
        String cardNumber = "1234567890123456";
        BankCard bankCard = new BankCard();
        bankCard.setCardNumber(cardNumber);
        bankCard.setBalance(BigDecimal.valueOf(1000));
        when(bankCardRepository.findByCardNumber(cardNumber)).thenReturn((bankCard));
        BigDecimal balance = atmService.checkBalance(cardNumber);
        assertEquals(BigDecimal.valueOf(1000), balance);
    }

    @Test
    public void testDeleteCardByNumber() throws CardNotFoundException {
        String cardNumber = "1234567890123456";
        BankCard bankCard = new BankCard();
        bankCard.setCardNumber(cardNumber);
        when(bankCardRepository.findByCardNumber(cardNumber)).thenReturn((bankCard));
        BankCard deletedCard = atmService.deleteCardByNumber(cardNumber);
        assertEquals((bankCard), deletedCard);
    }

    @Test
    public void testSendTransaction() throws CardNotFoundException {
        BankCard senderCard = new BankCard();
        senderCard.setBalance(BigDecimal.valueOf(1000));
        BankCard recipientCard = new BankCard();
        recipientCard.setBalance(BigDecimal.valueOf(500));
        BigDecimal amount = BigDecimal.valueOf(200);
        atmService.sendTransaction((senderCard), (recipientCard), amount);
        verify(bankCardRepository, times(1)).save(senderCard);
        verify(bankCardRepository, times(1)).save(recipientCard);
    }

    @Test
    void testDepositCashFromATM() throws CardNotFoundException {
        BankCard card = new BankCard(1L, "1234567890123456", "1111", BigDecimal.valueOf(500));
        BigDecimal amount = BigDecimal.valueOf(200);
        List<ATM> allAtms = new ArrayList<>();
        allAtms.add(new ATM(1L,"ATM1","null"));
        allAtms.add(new ATM(2L,"ATM2","null"));
        when(atmRepository.findAll()).thenReturn(allAtms);
        atmService.depositCashFromATM(card, amount);
        assertEquals(BigDecimal.valueOf(700), card.getBalance());
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void testWithdrawFromATM() throws CardNotFoundException {
        BankCard card = new BankCard(1L, "1234567890123456", "1111", BigDecimal.valueOf(500));
        BigDecimal amount = BigDecimal.valueOf(200);
        List<ATM> allAtms = new ArrayList<>();
        allAtms.add(new ATM(1L,"ATM1","null"));
        when(atmRepository.findAll()).thenReturn(allAtms);
        atmService.withdrawFromATM(card, amount);
        assertEquals(BigDecimal.valueOf(300), card.getBalance());
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void testCreateCard() {
        BankCard newCard = new BankCard(1L, "1234567890123456", "1111", BigDecimal.valueOf(0));
        when(bankCardRepository.save(any())).thenReturn(newCard);
        BankCard createdCard = atmService.createCard();
        assertNotNull(createdCard);
        assertEquals("1234567890123456", createdCard.getCardNumber());
        assertEquals("1111", createdCard.getPinNumber());
        assertEquals(BigDecimal.valueOf(0), createdCard.getBalance());
    }
}
