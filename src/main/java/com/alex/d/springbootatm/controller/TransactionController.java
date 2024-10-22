package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.repository.BankCardRepository;
import com.alex.d.springbootatm.response.ErrorResponse;
import com.alex.d.springbootatm.response.TransferResponse;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
@Slf4j
@RequestMapping("/api/v1")
@Tag(name = "Transactions")
public class TransactionController {

    @Autowired
    private ATMService atmService;


    @PostMapping("/transfer")
    @Operation(
            description = "Transfer funds between cards",
            summary = "Transfer funds between cards",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = TransferResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))}),
                    @ApiResponse(responseCode = "404", description = "Not found", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
                    })
            }
    )
    public ResponseEntity transferFundsToAnotherCard(
            @Parameter(description = "Sender card number", required = true) @RequestParam("senderCardNumber") String senderCardNumber,
            @Parameter(description = "Recipient card number", required = true) @RequestParam("recipientCardNumber") String recipientCardNumber,
            @Parameter(description = "Transfer amount", required = true) @RequestParam("amount") BigDecimal amount
    ) {


        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid deposit amount: {}", amount);
            ErrorResponse errorResponse = new ErrorResponse(Instant.now(), "400",
                    "Invalid transfer amount",
                    "/transfer/" + amount);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        BigDecimal senderBalance = atmService.checkBalanceByCardNumber(senderCardNumber).getBalance();

        if (senderBalance.compareTo(amount) < 0) {

            log.error("Insufficient funds on sender's card: {}", senderBalance);

            ErrorResponse errorResponse = new ErrorResponse(Instant.now(), "400", "Insufficient funds on sender's card", "/transfer/" + senderBalance);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            TransferResponse transferResponse = atmService.sendTransaction(senderCardNumber, recipientCardNumber, amount);
            log.info("Transactions of {} from card {} to card {} was successful.", amount, senderCardNumber, recipientCardNumber);
            return ResponseEntity.status(HttpStatus.OK).body(transferResponse);
        } catch (CardNotFoundException e) {

            ErrorResponse errorResponse = new ErrorResponse(Instant.now(), "404",
                    "Card not found",
                    "/transfer/");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}



