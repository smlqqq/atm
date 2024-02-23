package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.exception.InsufficientFundsException;
import com.alex.d.springbootatm.model.CardATM;
import com.alex.d.springbootatm.service.CardService;
import com.alex.d.springbootatm.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@Slf4j
@RequestMapping("/v1")
public class TransactionController {
    private final CardService cardService;

    private final TransactionService transactionService;

    public TransactionController(CardService cardService, TransactionService transactionService) {
        this.cardService = cardService;
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferFunds(
            @RequestParam("senderCardNumber") String senderCardNumber,
            @RequestParam("recipientCardNumber") String recipientCardNumber,
            @RequestParam("amount") BigDecimal amount
    ) {
        try {
            // Получение данных о картах отправителя и получателя из базы данных
            CardATM senderCard = cardService.findByCardNumber(senderCardNumber);
            CardATM recipientCard = cardService.findByCardNumber(recipientCardNumber);

            // Проверка наличия карт отправителя и получателя
            if (senderCard == null) {
                log.error("Sender card not found: {}", senderCardNumber);
                return ResponseEntity.badRequest().body("Sender card not found.");
            }
            if (recipientCard == null) {
                log.error("Recipient card not found: {}", recipientCardNumber);
                return ResponseEntity.badRequest().body("Recipient card not found.");
            }

            // Проверка достаточности средств на счете отправителя
            if (senderCard.getBalance().compareTo(amount) < 0) {
                log.error("Insufficient funds on sender's card: {}", senderCardNumber, new InsufficientFundsException("Insufficient funds Insufficient funds on sender's card."));
                log.error("Current balance on sender's card: {}", senderCard.getBalance());
                return ResponseEntity.badRequest().body("Insufficient funds on sender's card.");
            }

            // Отправка транзакции
            transactionService.sendTransaction(senderCard, recipientCard, amount);

            // Логирование успешной отправки транзакции
            log.info("Transaction of {} from card {} to card {} was successful.",
                    amount, senderCardNumber, recipientCardNumber);

            // Возвращение успешного HTTP-ответа
            return ResponseEntity.ok("Transaction sent successfully.");
        } catch (Exception e) {
            // Обработка других возможных ошибок и возврат HTTP-ответа с ошибкой
            log.error("Failed to send transaction: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to send transaction: " + e.getMessage());
        }
    }

    @PostMapping("/deposit/{cardNumber}")
    public ResponseEntity<String> deposit(
            @PathVariable String cardNumber,
            @RequestParam BigDecimal amount) {

        try {
            CardATM card = cardService.findByCardNumber(cardNumber);
            if (card == null) {
                log.error("Sender card not found: {}", cardNumber);
                return ResponseEntity.badRequest().body("Sender card not found.");
            }
            transactionService.deposit(cardNumber, amount);

            log.info("Deposit {} to card {} was successful.", amount, cardNumber);

            return ResponseEntity.ok("Money successfully deposited.");

        } catch (Exception | CardNotFoundException e) {
            // Обработка других возможных ошибок и возврат HTTP-ответа с ошибкой
            log.error("Failed to send transaction: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to send transaction: " + e.getMessage());
        }
    }
}

