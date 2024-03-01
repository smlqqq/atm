package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.repository.BankCardRepository;
import com.alex.d.springbootatm.response.BalanceResponse;
import com.alex.d.springbootatm.response.DepositResponse;
import com.alex.d.springbootatm.response.ErrorResponse;
import com.alex.d.springbootatm.response.WithdrawResponse;
import com.alex.d.springbootatm.service.ATMService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;

@Controller
@Slf4j
@RequestMapping("/api/v1")
@Tag(name = "Bank card")
public class ATMController {

    @Autowired
    private ATMService atmService;

    @Autowired
    private BankCardRepository bankCardRepository;

    public ATMController(BankCardRepository bankCardRepository, ATMService atmService) {
        this.bankCardRepository = bankCardRepository;
        this.atmService = atmService;
    }



     @Operation(
            operationId = "balanceGet",
            summary = "Get account balance",
            tags = {"Account"},
            description = "Retrieve bank card details by providing the card number",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Account balance retrieved successfully", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = BalanceResponse.class))}),
                    @ApiResponse(responseCode = "404", description = "Not found", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))}),

            }
    )

    @GetMapping("/balance/{cardNumber}")
    public ResponseEntity getBalance(@PathVariable String cardNumber) {


        try {
            BankCard bankCard = bankCardRepository.findByCardNumber(cardNumber);
            if (bankCard == null) {
                log.error("Invalid credit card number {}", cardNumber);
                ErrorResponse errorResponse = new ErrorResponse(Instant.now(), 404, "Card not found", "/balance/" + cardNumber);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            BigDecimal balance = atmService.checkBalance(cardNumber);
            log.info("Card: {} Balance: {}", cardNumber, balance);

            BalanceResponse response = new BalanceResponse(cardNumber, balance);


            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (CardNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @Operation(
            summary = "Deposit funds to the specified card",
            description = "Deposit funds to the specified card using ATM and amount",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Funds deposited successfully", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = DepositResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))}),
                    @ApiResponse(responseCode = "404", description = "Card not found", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))}),
            }
    )
    @PutMapping("/deposit")
    public ResponseEntity depositCash(
            @Parameter(description = "Recipient card number", required = true) @RequestParam("cardNumber") String recipientCardNumber,
            @Parameter(description = "Amount to deposit", required = true) @RequestParam("amount") BigDecimal amount) {

        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                log.error("Invalid deposit amount: {}", amount);
                ErrorResponse errorResponse = new ErrorResponse(Instant.now(), 400, "Invalid deposit amount", "/deposit/" + recipientCardNumber);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            BankCard recipientCard = bankCardRepository.findByCardNumber(recipientCardNumber);
            if (recipientCard == null) {
                log.error("Invalid credit card number {}", recipientCardNumber);
                ErrorResponse errorResponse = new ErrorResponse(Instant.now(),404, "Card not found", "/deposit/" + recipientCardNumber);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            BigDecimal recipientCardBalance = recipientCard.getBalance();
            atmService.depositCashFromATM(recipientCard, amount);
            log.info("Deposit of {} to card {} was successful.", amount, recipientCardNumber);

            DepositResponse depositResponse = new DepositResponse(recipientCardNumber, recipientCardBalance.add(amount));
            return ResponseEntity.ok(depositResponse);
        } catch (CardNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @Operation(
            summary = "Withdraw funds from ATM",
            description = "Withdraw funds from the specified card using the provided card number and amount",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Funds successfully withdrawn", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = WithdrawResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Failed to withdraw funds", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))}),
                    @ApiResponse(responseCode = "404", description = "Not found", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
                    })
            }
    )
    @PostMapping("/withdraw")
    public ResponseEntity withdraw(
            @Parameter(description = "Card number", required = true) @RequestParam("cardNumber") String cardNumber,
            @Parameter(description = "Amount to withdraw", required = true) @RequestParam("amount") BigDecimal amount) {

        try {
            BankCard recipientCard = bankCardRepository.findByCardNumber(cardNumber);
            if (recipientCard == null) {
                log.error("Invalid credit card number {}", cardNumber);
                ErrorResponse errorResponse = new ErrorResponse(Instant.now(),404, "Card not found", "/withdraw/" + cardNumber);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            BigDecimal senderBalance = recipientCard.getBalance();
            if (senderBalance.compareTo(amount) < 0) {
                log.error("Insufficient funds on your card: {}", recipientCard);
                ErrorResponse errorResponse = new ErrorResponse(Instant.now(), 400, "Failed to withdraw funds","/withdraw" + cardNumber + " " + amount);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            atmService.withdrawFromATM(recipientCard, amount);

            log.info("Withdrawal of {} from card {} was successful.", amount, cardNumber);

            WithdrawResponse response = new WithdrawResponse(cardNumber, amount.toString(), senderBalance.subtract(amount));
            return ResponseEntity.ok(response);

        } catch (CardNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
