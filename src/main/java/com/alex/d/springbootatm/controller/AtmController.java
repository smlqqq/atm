package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.model.response.BalanceResponse;
import com.alex.d.springbootatm.model.response.DepositeResponse;
import com.alex.d.springbootatm.model.response.ErrorResponse;
import com.alex.d.springbootatm.model.response.WithdrawResponse;
import com.alex.d.springbootatm.service.atm.AtmService;
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
public class AtmController {

    @Autowired
    private AtmService atmService;

    @Operation(
            summary = "Get account balance.",
            description = "Returns the balance for the provided card number.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = BalanceResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))}),
                    @ApiResponse(responseCode = "404", description = "Not found", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
                    })
            }
    )
    @GetMapping("/balance/{card}")
    public ResponseEntity balance(@PathVariable String card) {

        if (card.isEmpty()) {
            log.error("invalid card number {}", card);
            ErrorResponse badRequest = new ErrorResponse(
                    Instant.now(),
                    "400",
                    "Invalid card number.",
                    "/balance/" + card
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(badRequest);
        }

        try {
            BalanceResponse response = atmService.checkBalanceByCardNumber(card);
            log.info("balance {} for card {} retreived successfuly", response.getBalance(), card);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (CardNotFoundException e) {
            log.error("Card not found for number: {}", card);
            ErrorResponse notFound = new ErrorResponse(
                    Instant.now(),
                    "404",
                    "Card not found",
                    "/balance/" + card
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
        }

    }


    @Operation(
            summary = "Deposit funds to the specified card",
            description = "Deposit funds to the specified card using ATM and amount",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = DepositeResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))}),
                    @ApiResponse(responseCode = "404", description = "Not found", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))}),
            }
    )
    @PutMapping("/deposit")
    public ResponseEntity deposit(
            @Parameter(description = "Recipient card number", required = true) @RequestParam("card") String card,
            @Parameter(description = "Amount to deposit", required = true) @RequestParam("amount") BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid deposit amount: {}", amount);
            ErrorResponse errorResponse = new ErrorResponse(Instant.now(),
                    "400",
                    "Invalid deposit amount",
                    "/deposit/" + amount);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {

            DepositeResponse depositResponse = atmService.updateAccountBalance(card, amount, true);
            log.info("Balance {} increased successfully for card {}", amount, card);

            return ResponseEntity.status(HttpStatus.OK).body(depositResponse);
        } catch (CardNotFoundException e) {
            log.error("Card not found: {}", card);
            ErrorResponse errorResponse = new ErrorResponse(Instant.now(),
                    "404",
                    "Card not found",
                    "/deposit/" + card);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

    }

    @Operation(
            summary = "Withdraw funds from ATM",
            description = "Withdraw funds from the specified card using the provided card number and amount",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = WithdrawResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))}),
                    @ApiResponse(responseCode = "404", description = "Not found", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
                    })
            }
    )
    @PostMapping("/withdraw")
    public ResponseEntity withdraw(
            @Parameter(description = "Card number", required = true) @RequestParam("card") String card,
            @Parameter(description = "Amount to withdraw", required = true) @RequestParam("amount") BigDecimal amount) {

        BigDecimal cardBalance = atmService.checkBalanceByCardNumber(card).getBalance();

        if (cardBalance.compareTo(amount) < 0) {
            log.error("Insufficient funds on your card: {} balance: {}, requested withdrawal: {}", card, cardBalance, amount);
            ErrorResponse errorResponse = new ErrorResponse(
                    Instant.now(),
                    "400",
                    "Failed to withdraw funds. Insufficient balance: " + cardBalance,
                    "/withdraw/" + card
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid withdraw amount: {}", amount);
            ErrorResponse errorResponse = new ErrorResponse(Instant.now(),
                    "400",
                    "Negative amount, or equals to zero",
                    "/withdraw/" + amount);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            DepositeResponse withdrawResponse = atmService.updateAccountBalance(card, amount, false);
            log.info("Balance for card {} decreased {}", card, withdrawResponse.getDeposit());
            return ResponseEntity.status(HttpStatus.OK).body(withdrawResponse);
        } catch (CardNotFoundException e) {
            log.error("Card not found: {}", card);
            ErrorResponse errorResponse = new ErrorResponse(Instant.now(),
                    "404",
                    "Card not found",
                    "/withdraw/" + card);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

}
