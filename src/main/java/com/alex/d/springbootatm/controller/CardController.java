package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.model.CardATM;
import com.alex.d.springbootatm.repository.CardATMRepository;
import com.alex.d.springbootatm.service.CardService;
import com.alex.d.springbootatm.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/v1")
public class CardController {

    private final CardService cardService;
    private final CardATMRepository cardATMRepository;

    public CardController( CardService cardService, CardATMRepository cardATMRepository) {
        this.cardService = cardService;
        this.cardATMRepository = cardATMRepository;
    }

    @GetMapping("/card")
    public ResponseEntity<CardATM> cardForm() {
        log.info("Handling /api/card request");
        // Генерация номера карты и пин-кода
        CardATM card = cardService.createCard();
        // Возвращение сгенерированной карты в качестве ответа
        log.info("Generated card: {}", card);

        return new ResponseEntity<>(card, HttpStatus.OK);
    }

    @GetMapping("/card/{cardNumber}")
    public ResponseEntity<CardATM> getCardByNumber(@PathVariable String cardNumber) {
        CardATM card = cardService.findByCardNumber(cardNumber);
        if (card == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(card);
    }

    @PostMapping
    public ResponseEntity<CardATM> createCard(@RequestBody CardATM card) {
        log.info("Creating new card: {}", card);
        CardATM createdCard = cardService.saveCard(card);
        log.info("New card created: {}", createdCard);
        return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
    }


    @GetMapping("/cards")
    public ResponseEntity<List<CardATM>> getAllCards() {
        List<CardATM> cards = cardATMRepository.findAll();
        log.info("Retrieved {} cards from the database", cards.size());
        return ResponseEntity.ok(cards);
    }





//    @PostMapping("/card")
//    public ResponseEntity<String> cardSubmit(@RequestParam("cardNumber") String cardNumber,
//                                             @RequestParam("pinCode") String pinCode) {
//        // Здесь можно добавить логику проверки номера карты и пин-кода
//
//        // Возвращение строки с перенаправлением в качестве ответа
//        return new ResponseEntity<>("redirect:/personalPage", HttpStatus.OK);
//    }

}