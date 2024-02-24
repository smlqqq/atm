package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.exception.InsufficientFundsException;
import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.service.CardService;
import com.alex.d.springbootatm.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/v1")
@Tag(name = "Transactions")


public class TransactionController {


    private final CardService cardService;

    private final TransactionService transactionService;

    public TransactionController(CardService cardService, TransactionService transactionService) {
        this.cardService = cardService;
        this.transactionService = transactionService;
    }

    @Operation(
            description = "Get endpoint for transfer",
            summary = "Transfer funds between cards",
            responses =
            @ApiResponse(
                    description = "Success",
                    responseCode = "200"
            )

    )

    @PostMapping("/transfer")
    public ResponseEntity<String> transferFunds(
            @Parameter(description = "Sender card number", required = true) @RequestParam("senderCardNumber") String senderCardNumber,
            @Parameter(description = "Recipient card number", required = true) @RequestParam("recipientCardNumber") String recipientCardNumber,
            @Parameter(description = "Transfer amount", required = true)
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

    @Operation(
            summary = "Deposit funds to the specified card",
            description = "Deposit funds to the specified card using ATM and amount",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Funds successfully deposited"),
                    @ApiResponse(responseCode = "400", description = "Failed to deposit funds")
            }
    )
    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(
            @Parameter(description = "Recipient card number", required = true) @RequestParam("cardNumber") String recipientCardNumber,
            @Parameter(description = "Amount to deposit", required = true) @RequestParam("amount") BigDecimal amount) throws CardNotFoundException {

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

    @Operation(
            summary = "Withdraw funds from ATM",
            description = "Withdraw funds from the specified card using the provided card number and amount",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Funds successfully withdrawn"),
                    @ApiResponse(responseCode = "400", description = "Failed to withdraw funds")
            }
    )
    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(
            @Parameter(description = "Card number", required = true) @RequestParam("cardNumber") String cardNumber,
            @Parameter(description = "Amount to withdraw", required = true) @RequestParam("amount") BigDecimal amount) throws CardNotFoundException, InsufficientFundsException {

        try {
            Optional<BankCard> card = cardService.findByCardNumber(cardNumber);
            if (card.isEmpty()) {
                log.error("Card not found: {}", cardNumber, new CardNotFoundException("Card not found."));
                return ResponseEntity.badRequest().body("Card not found.");
            }

            transactionService.withdrawFromATM(card, amount); // Передача объекта BankCard

            log.info("Withdrawal of {} from card {} was successful.", amount, cardNumber);

            return ResponseEntity.ok("Money successfully withdrawn.");

        } catch (Exception e) {
            log.error("Failed to withdraw money: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to withdraw money: " + e.getMessage());
        }
    }
}

