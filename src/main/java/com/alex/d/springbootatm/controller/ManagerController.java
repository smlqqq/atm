package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.model.BankCardModel;
import com.alex.d.springbootatm.response.ErrorResponse;
import com.alex.d.springbootatm.service.ATMService;
import com.alex.d.springbootatm.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Cards Management")
public class ManagerController {

    @Autowired
    private ATMService atmService;

    @Autowired
    private ReportService reportService;


    @Operation(
            summary = "Get all bank cards",
            description = "Retrieve details of all bank cards",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = BankCardModel.class))
                    })
            }
    )
    @GetMapping("/bank-cards")
    public ResponseEntity<List<BankCardModel>> getAllBankCards() {
        return atmService.getAllCards()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(
            summary = "Delete card",
            description = "Delete all details about card",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = BankCardModel.class))}),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))}),
                    @ApiResponse(responseCode = "404", description = "Not found", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))}),
            }
    )
    @DeleteMapping("/deleteCard/{cardNumber}")
    public ResponseEntity deleteCard(@PathVariable("cardNumber") String cardNumber) {

        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            log.error("Invalid credit card number {}", cardNumber);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Card number cannot be null or empty");
        }

        try {
            atmService.deleteCardByNumber(cardNumber);
            return ResponseEntity.status(HttpStatus.OK).body("Card with number " + cardNumber + " was deleted");
        } catch (CardNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(Instant.now(), "404", "Card not found", "/delete/" + cardNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

    }

    @Operation(
            summary = "Create new bank card",
            description = "Create a new bank card using the provided details",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = BankCardModel.class))
                    })
            }

    )

    @PostMapping("/createCard")
    public ResponseEntity createNewCard() {
        return ResponseEntity.status(HttpStatus.CREATED).body(atmService.createCard());
    }


    @Operation(
            summary = "Download",
            description = "Download all data.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = {
                            @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")}),
                    @ApiResponse(responseCode = "404", description = "Not Found", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))}),
            }
    )
    @GetMapping("/bank-cards/export/excel")
    public ResponseEntity exportAllBankCardsReportToExcel() {
        return reportService.generateClientReport();
    }

    @Operation(
            summary = "Download",
            description = "Download data by a card number.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = {
                            @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")}),
                    @ApiResponse(responseCode = "404", description = "Not Found", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))}),
            }
    )

    @GetMapping("/bank-cards/export/excel/{cardNumber}")
    public ResponseEntity exportIndividualClientReportToExcel(@PathVariable("cardNumber") String cardNumber) {
        return reportService.generateIndividualClientReport(cardNumber);
    }

}
