package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.model.dto.response.ErrorResponse;
import com.alex.d.springbootatm.model.dto.response.TransactionResponse;
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
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
@Slf4j
@RequestMapping("/api/v1")
@Tag(name = "Transactions")
public class TransactionController {

    @Autowired
    private AtmService atmService;


    @PutMapping("/transfer")
    @Operation(
            description = "Transfer funds between cards",
            summary = "Transfer funds between cards",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = TransactionResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))}),
                    @ApiResponse(responseCode = "404", description = "Not found", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
                    })
            }
    )
    public ResponseEntity transferFundsToAnotherCard(
            @Parameter(description = "Sender card number", required = true) @RequestParam("sender") String sender,
            @Parameter(description = "Recipient card number", required = true) @RequestParam("recipient") String recipient,
            @Parameter(description = "Transfer amount", required = true) @RequestParam("amount") BigDecimal amount
    ) {


        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid deposit amount: {}", amount);
            ErrorResponse errorResponse = new ErrorResponse(Instant.now(), "400",
                    "Invalid transfer amount",
                    "/transfer/" + amount);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }


        if (atmService.checkBalanceByCardNumber(sender).getBalance().compareTo(amount) < 0) {
            log.error("Insufficient funds on card:");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(Instant.now(),
                    "400",
                    "Insufficient funds on card",
                    "/transfer/" + sender));
        }

        try {
            log.info("Transactions of {} from card {} to card {} was successful.", amount, sender, recipient);
            TransactionResponse transactionResponse = atmService.transferBetweenCards(sender, recipient, amount);
            return ResponseEntity.status(HttpStatus.OK).body(transactionResponse);
        } catch (CardNotFoundException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(Instant.now(), "404",
                    "Card not found",
                    "/transfer/"));
        }
    }
}



