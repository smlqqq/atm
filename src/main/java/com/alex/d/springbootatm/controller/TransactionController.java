package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.exception.InsufficientFundsException;
import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.repository.BankCardRepository;
import com.alex.d.springbootatm.service.ATMService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/v1")
@Tag(name = "Transactions")
public class TransactionController {

    private final BankCardRepository bankCardRepository;
    private final ATMService atmService;

    public TransactionController(BankCardRepository bankCardRepository, ATMService atmService) {
        this.bankCardRepository = bankCardRepository;
        this.atmService = atmService;
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
            Optional<BankCard> senderCard = bankCardRepository.findByCardNumber(senderCardNumber);
            Optional<BankCard> recipientCard = bankCardRepository.findByCardNumber(recipientCardNumber);

            if (senderCard.isEmpty()) {
                log.error("Sender card not found: {}", senderCardNumber, new CardNotFoundException("Sender card not found exception."));
                return ResponseEntity.badRequest().body("Sender card not found.");
            }

            if (recipientCard.isEmpty()) {
                log.error("Recipient card not found: {}", recipientCardNumber, new CardNotFoundException("Recipient card not found exception."));
                return ResponseEntity.badRequest().body("Recipient card not found.");
            }

            BigDecimal senderBalance = senderCard.get().getBalance();
            if (senderBalance.compareTo(amount) < 0) {
                log.error("Insufficient funds on sender's card: {}", senderCardNumber, new InsufficientFundsException("Insufficient funds on sender's card."));
                return ResponseEntity.badRequest().body("Insufficient funds on sender's card.");
            }

            atmService.sendTransaction(senderCard, recipientCard, amount);

            log.info("Transactions of {} from card {} to card {} was successful.",
                    amount, senderCardNumber, recipientCardNumber);

            return ResponseEntity.ok("Transactions sent successfully.");
        } catch (Exception e) {
            log.error("Failed to send transaction: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to send transaction: " + e.getMessage());
        }
    }

}

