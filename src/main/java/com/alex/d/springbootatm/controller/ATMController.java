package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.repository.CardATMRepository;
import com.alex.d.springbootatm.service.ATMService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final CardATMRepository cardATMRepository;

    public ATMController(ATMService atmService, CardATMRepository cardATMRepository) {
        this.atmService = atmService;
        this.cardATMRepository = cardATMRepository;
    }

    @Operation(
            summary = "Create new bank card",
            description = "Create a new bank card using the provided details",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Bank card created successfully")
            }
    )
    @PostMapping("/card")
    public ResponseEntity<BankCard> createNewCard(@RequestBody BankCard card) {
        log.info("Creating new card: {}", card);
        BankCard createdCard = atmService.createCard();
        log.info("New card created: {}", createdCard);
        return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
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
    public ResponseEntity<BigDecimal> getBalance (
            @PathVariable Long cardNumber
    ) throws CardNotFoundException {
        return ResponseEntity.ok(atmService.checkBalance(String.valueOf(cardNumber)));
    }


//    @GetMapping("/balance/{cardNumber}")
//    public ResponseEntity<Optional<BankCard>> getBalance(@PathVariable Long cardNumber) {
//        try {
//            Optional<BankCard> card = atmService.findByCardNumber(String.valueOf(cardNumber));
//            if (card.isEmpty()) {
//                throw new CardNotFoundException("Card not found");
//            }
//            return ResponseEntity.ok(card);
//        } catch (CardNotFoundException e) {
//            log.error("Card not found: {}", cardNumber, e);
//            return ResponseEntity.notFound().build();
//        }
//    }
}


//    @Operation(
//            summary = "Delete bank card",
//            description = "Delete the bank card associated with the provided card number",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Card successfully deleted"),
//                    @ApiResponse(responseCode = "400", description = "Failed to delete card")
//            }
//    )
//    @DeleteMapping("/card")
//    public ResponseEntity<String> deleteCard(
//            @Parameter(description = "Card number", required = true) @RequestParam("cardNumber") String cardNumber) {
//
//        try {
//            Optional<BankCard> card = cardService.findByCardNumber(cardNumber);
//            if (card.isEmpty()) {
//                log.error("Card not found: {}", cardNumber, new CardNotFoundException("Card not found."));
//                return ResponseEntity.badRequest().body("Card not found.");
//            }
//
//            cardService.deleteCard(card);
//
//            log.info("Card {} was successfully deleted.", cardNumber);
//
//            return ResponseEntity.ok("Card successfully deleted.");
//
//        } catch (Exception e) {
//            log.error("Failed to delete card: {}", e.getMessage());
//            return ResponseEntity.badRequest().body("Failed to delete card: " + e.getMessage());
//        }
//    }







//    @PostMapping("/card")
//    public ResponseEntity<String> cardSubmit(@RequestParam("cardNumber") String cardNumber,
//                                             @RequestParam("pinCode") String pinCode) {
//        // Здесь можно добавить логику проверки номера карты и пин-кода
//
//        // Возвращение строки с перенаправлением в качестве ответа
//        return new ResponseEntity<>("redirect:/personalPage", HttpStatus.OK);
//    }

