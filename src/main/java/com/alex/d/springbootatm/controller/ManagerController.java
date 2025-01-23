package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.dto.BankCardDto;
import com.alex.d.springbootatm.model.response.ErrorResponse;
import com.alex.d.springbootatm.service.card.BankCardService;
import com.alex.d.springbootatm.util.ReportService;
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
@Tag(name = "Manager")
public class ManagerController {

    @Autowired
    private BankCardService bankCardService;

    @Autowired
    private ReportService reportService;


    @Operation(
            summary = "Get all data",
            description = "Retrieve details of all bank cards",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = BankCardDto.class))
                    })
            }
    )
    @GetMapping("/bank-cards/all")
    public ResponseEntity<List<BankCardDto>> cards() {
        List<BankCardDto> cards = bankCardService.getAllCards();
        if (cards.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(cards);
    }

    @Operation(
            summary = "Delete card",
            description = "Delete all details about card",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = BankCardDto.class))}),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))}),
                    @ApiResponse(responseCode = "404", description = "Not found", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))}),
            }
    )
    @DeleteMapping("/delete/{card}")
    public ResponseEntity delete(@PathVariable("card") String card) {

        if (card == null || card.trim().isEmpty()) {
            log.error("Invalid credit card number {}", card);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Card number cannot be null or empty");
        }

        try {
            BankCardDto bankCardDto = bankCardService.deleteCardByNumber(card);
            return ResponseEntity.status(HttpStatus.OK).body(BankCardDto.builder()
                    .cardNumber(bankCardDto.getCardNumber())
                    .pin(bankCardDto.getPin())
                    .balance(bankCardDto.getBalance())
                    .build()
            );
        } catch (CardNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(Instant.now(), "404", "Card not found", "/delete/" + card);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

    }

    @Operation(
            summary = "Create new bank card",
            description = "Create a new bank card using the provided details",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", content = {
                            @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = BankCardDto.class))
                    })

            }

    )

    @PostMapping("/create")
    public ResponseEntity create() {
        BankCardDto card = bankCardService.createCard();
        return ResponseEntity.status(HttpStatus.CREATED).body(card);
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
    @GetMapping("/cards/export/excel")
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

    @GetMapping("/cards/export/excel/{card}")
    public ResponseEntity exportIndividualClientReportToExcel(@PathVariable("card") String card) {
        return reportService.generateIndividualClientReport(card);
    }

}
