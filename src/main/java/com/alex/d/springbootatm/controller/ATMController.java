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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@Slf4j
@RequestMapping("/api/v1")
@Tag(name="Bank card")
public class ATMController {

    private final ATMService atmService;
    private final BankCardRepository bankCardRepository;

    public ATMController(ATMService atmService, BankCardRepository bankCardRepository) {
        this.atmService = atmService;
        this.bankCardRepository = bankCardRepository;
    }

    @Operation(
            summary = "Check balance",
            description = "Retrieve bank card details by providing the card number",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            }
    )

    @GetMapping("/balance/{cardNumber}")
    public ResponseEntity<BigDecimal> getBalance(
            @PathVariable Long cardNumber
    ) throws CardNotFoundException {
        log.info("Card: {} Balance: {}", cardNumber, atmService.checkBalance(String.valueOf(cardNumber)));
        return ResponseEntity.ok(atmService.checkBalance(String.valueOf(cardNumber)));
    }

    @Operation(
            summary = "Deposit funds to the specified card",
            description = "Deposit funds to the specified card using ATM and amount",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Funds successfully deposited"),
                    @ApiResponse(responseCode = "400", description = "Failed to deposit funds")
            }
    )
    @PutMapping("/deposit")
    public ResponseEntity<String> depositCash(
            @Parameter(description = "Recipient card number", required = true) @RequestParam("cardNumber") String recipientCardNumber,
            @Parameter(description = "Amount to deposit", required = true) @RequestParam("amount") BigDecimal amount) throws CardNotFoundException {

        try {
            BankCard recipientCard = bankCardRepository.findByCardNumber(recipientCardNumber);
            if (recipientCard==null) {
                log.error("Card not found: {}", recipientCardNumber, new CardNotFoundException("Card not found."));
                return ResponseEntity.badRequest().body("Card not found.");
            }

            atmService.depositCashFromATM(recipientCard, amount);

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
            @Parameter(description = "Amount to withdraw", required = true) @RequestParam("amount") BigDecimal amount) throws CardNotFoundException{

        try {
            BankCard card = bankCardRepository.findByCardNumber(cardNumber);
            if (card==null) {
                log.error("Card not found: {}", cardNumber, new CardNotFoundException("Card not found."));
                return ResponseEntity.badRequest().body("Card not found.");
            }

            BigDecimal senderBalance = card.getBalance();
            if (senderBalance.compareTo(amount) < 0) {
                log.error("Insufficient funds on your card: {}", card, new InsufficientFundsException("Insufficient funds on your card."));
                return ResponseEntity.badRequest().body("Insufficient funds on your card.");
            }

            atmService.withdrawFromATM(card, amount);

            log.info("Withdrawal of {} from card {} was successful.", amount, cardNumber);

            return ResponseEntity.ok("Money successfully withdrawn.");

        } catch (Exception e) {
            log.error("Failed to withdraw money: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to withdraw money: " + e.getMessage());
        }
    }
}