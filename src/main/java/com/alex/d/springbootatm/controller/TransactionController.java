package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.exception.InsufficientFundsException;
import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.service.CardService;
import com.alex.d.springbootatm.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

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
    ) throws CardNotFoundException {
        try {
            Optional<BankCard> senderCard = cardService.findByCardNumber(senderCardNumber);
            Optional<BankCard> recipientCard = cardService.findByCardNumber(recipientCardNumber);

            if (senderCard.isEmpty()) {
                log.error("Sender card not found: {}", senderCardNumber);
                return ResponseEntity.badRequest().body("Sender card not found.");
            }

            if (recipientCard.isEmpty()) {
                log.error("Recipient card not found: {}", recipientCardNumber);
                return ResponseEntity.badRequest().body("Recipient card not found.");
            }

            BigDecimal senderBalance = senderCard.get().getBalance();
            if (senderBalance.compareTo(amount) < 0) {
                log.error("Insufficient funds on sender's card: {}", senderCardNumber, new InsufficientFundsException("Insufficient funds on sender's card."));
                return ResponseEntity.badRequest().body("Insufficient funds on sender's card.");
            }

            transactionService.sendTransaction(senderCard, recipientCard, amount);

            log.info("Transaction of {} from card {} to card {} was successful.",
                    amount, senderCardNumber, recipientCardNumber);

            return ResponseEntity.ok("Transaction sent successfully.");
        } catch (Exception e) {
            log.error("Failed to send transaction: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to send transaction: " + e.getMessage());
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(
            @RequestParam("cardNumber") String recipientCardNumber,
            @RequestParam("amount") BigDecimal amount) throws CardNotFoundException {

        try {
            Optional<BankCard> recipientCard = cardService.findByCardNumber(recipientCardNumber);
            if (recipientCard.isEmpty()) {
                log.error("Card not found: {}", recipientCardNumber, new CardNotFoundException("Card not found."));
                return ResponseEntity.badRequest().body("Card not found.");
            }

            transactionService.depositFromATM(recipientCard, amount); // Передача объекта BankCard

            log.info("Deposit of {} to card {} was successful.", amount, recipientCardNumber);

            return ResponseEntity.ok("Money successfully deposited.");

        } catch (Exception e) {
            log.error("Failed to deposit money: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to deposit money: " + e.getMessage());
        }
    }
}

