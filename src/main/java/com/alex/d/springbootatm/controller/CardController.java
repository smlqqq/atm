package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.exception.CardNotFoundException;
import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.repository.CardATMRepository;
import com.alex.d.springbootatm.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/v1")
@Tag(name="Bank card")
public class CardController {

    private final CardService cardService;
    private final CardATMRepository cardATMRepository;

    public CardController(CardService cardService, CardATMRepository cardATMRepository) {
        this.cardService = cardService;
        this.cardATMRepository = cardATMRepository;
    }

    @Operation(
            summary = "Create new bank card",
            description = "Create a new bank card using the provided details",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Bank card created successfully")
            }
    )
    @GetMapping("/card")
    public ResponseEntity<BankCard> cardForm() {
        log.info("Handling /api/card request");
        // Генерация номера карты и пин-кода
        BankCard card = cardService.createCard();
        // Возвращение сгенерированной карты в качестве ответа
        log.info("Generated card: {}", card);

        return new ResponseEntity<>(card, HttpStatus.OK);
    }

    @Operation(
            summary = "Get bank card by card number",
            description = "Retrieve bank card details by providing the card number",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            }
    )
    @GetMapping("/card/{cardNumber}")
    public ResponseEntity<Optional<BankCard>> getCardByNumber(@PathVariable Long cardNumber) {
        try {
            Optional<BankCard> card = cardService.findByCardNumber(String.valueOf(cardNumber));
            if (card.isEmpty()) {
                throw new CardNotFoundException("Card not found");
            }
            return ResponseEntity.ok(card);
        } catch (CardNotFoundException e) {
            log.error("Card not found: {}", cardNumber, e);
            return ResponseEntity.notFound().build();
        }
    }

//    @Operation(
//            summary = "Create new bank card",
//            description = "Create a new bank card using the provided details",
//            responses = {
//                    @ApiResponse(responseCode = "201", description = "Bank card created successfully")
//            }
//    )
    @PostMapping("/card")
    public ResponseEntity<BankCard> createCard(@RequestBody BankCard card) {
        log.info("Creating new card: {}", card);
        BankCard createdCard = cardService.saveCard(card);
        log.info("New card created: {}", createdCard);
        return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get all bank cards",
            description = "Retrieve details of all bank cards",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success")
            }
    )
    @GetMapping("/cards")
    public ResponseEntity<List<BankCard>> getAllCards() {
        List<BankCard> cards = cardATMRepository.findAll();
        log.info("Retrieved {} cards from the database", cards.size());
        return ResponseEntity.ok(cards);
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


}




//    @PostMapping("/card")
//    public ResponseEntity<String> cardSubmit(@RequestParam("cardNumber") String cardNumber,
//                                             @RequestParam("pinCode") String pinCode) {
//        // Здесь можно добавить логику проверки номера карты и пин-кода
//
//        // Возвращение строки с перенаправлением в качестве ответа
//        return new ResponseEntity<>("redirect:/personalPage", HttpStatus.OK);
//    }

