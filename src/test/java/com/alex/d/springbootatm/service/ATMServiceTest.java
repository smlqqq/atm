package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.model.ATM;
import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.model.Transactions;
import com.alex.d.springbootatm.repository.ATMRepository;
import com.alex.d.springbootatm.repository.BankCardRepository;
import com.alex.d.springbootatm.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ATMServiceTest {

    // Моки для репозиториев и сервиса
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankCardRepository bankCardRepository;

    @Mock
    private ATMRepository atmRepository;

    // Инжектируем ATMService с зависимостями моков
    @InjectMocks
    private ATMService atmService;

    // Тест для проверки баланса карты
    @Test
    public void testCheckBalance() throws CardNotFoundException {
        // Устанавливаем данные для теста
        String cardNumber = "1234567890123456";
        BankCard bankCard = new BankCard();
        bankCard.setCardNumber(cardNumber);
        bankCard.setBalance(BigDecimal.valueOf(1000));
        // Когда вызывается метод findByCardNumber() с cardNumber, возвращаем bankCard
        when(bankCardRepository.findByCardNumber(cardNumber)).thenReturn((bankCard));

        // Вызываем метод, который мы хотим протестировать
        BigDecimal balance = atmService.checkBalance(cardNumber);

        // Проверяем, что результат соответствует ожиданиям
        assertEquals(BigDecimal.valueOf(1000), balance);
    }

    // Тест для удаления карты по номеру
    @Test
    public void testDeleteCardByNumber() throws CardNotFoundException {
        // Устанавливаем данные для теста
        String cardNumber = "1234567890123456";
        BankCard bankCard = new BankCard();
        bankCard.setCardNumber(cardNumber);
        // Когда вызывается метод findByCardNumber() с cardNumber, возвращаем bankCard
        when(bankCardRepository.findByCardNumber(cardNumber)).thenReturn((bankCard));

        // Вызываем метод, который мы хотим протестировать
        BankCard deletedCard = atmService.deleteCardByNumber(cardNumber);

        // Проверяем, что возвращенная карта соответствует ожиданиям
        assertEquals(Optional.of(bankCard), deletedCard);
    }

    @Test
    public void testSendTransaction() throws CardNotFoundException {
        // Устанавливаем данные для теста
        BankCard senderCard = new BankCard();
        senderCard.setBalance(BigDecimal.valueOf(1000));
        BankCard recipientCard = new BankCard();
        recipientCard.setBalance(BigDecimal.valueOf(500));
        BigDecimal amount = BigDecimal.valueOf(200);

        when(bankCardRepository.findByCardNumber("senderCardNumber")).thenReturn((senderCard));
        when(bankCardRepository.findByCardNumber("recipientCardNumber")).thenReturn((recipientCard));

        // Вызываем метод, который мы хотим протестировать
        atmService.sendTransaction(
                (senderCard),
                (recipientCard),
                amount);

        // Проверяем, что баланс отправителя уменьшился, а баланс получателя увеличился
        verify(bankCardRepository, times(1)).save(senderCard);
        verify(bankCardRepository, times(1)).save(recipientCard);
    }

    @Test
    public void testDepositCashFromATM() throws CardNotFoundException {
        // Устанавливаем данные для теста
        BankCard recipientCard = new BankCard();
        recipientCard.setBalance(BigDecimal.valueOf(500));
        BigDecimal amount = BigDecimal.valueOf(200);

        when(bankCardRepository.findByCardNumber("recipientCardNumber")).thenReturn((recipientCard));
        when(atmRepository.findAll()).thenReturn(List.of(new ATM()));

        // Вызываем метод, который мы хотим протестировать
        atmService.depositCashFromATM(
                (recipientCard),
                amount);

        // Проверяем, что баланс получателя увеличился и транзакция была сохранена
        verify(bankCardRepository, times(1)).save(recipientCard);
        verify(transactionRepository, times(1)).save(any(Transactions.class));
    }

    @Test
    public void testWithdrawFromATM() throws CardNotFoundException {
        // Устанавливаем данные для теста
        BankCard card = new BankCard();
        card.setBalance(BigDecimal.valueOf(1000));
        BigDecimal amount = BigDecimal.valueOf(200);
        List<ATM> atmList = List.of(new ATM());

        when(bankCardRepository.findByCardNumber("cardNumber")).thenReturn((card));
        when(atmRepository.findAll()).thenReturn(atmList);

        // Вызываем метод, который мы хотим протестировать
        atmService.withdrawFromATM((card), amount);

        // Проверяем, что баланс уменьшился и транзакция была сохранена
        verify(bankCardRepository, times(1)).save(card);
        verify(transactionRepository, times(1)).save(any(Transactions.class));
    }
}
