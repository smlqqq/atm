package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.repository.CardATMRepository;
import com.alex.d.springbootatm.service.CardService;
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
public class CardController {

    private final CardService cardService;
    private final CardATMRepository cardATMRepository;

    public CardController( CardService cardService, CardATMRepository cardATMRepository) {
        this.cardService = cardService;
        this.cardATMRepository = cardATMRepository;
    }

    @GetMapping("/card")
    public ResponseEntity<BankCard> cardForm() {
        log.info("Handling /api/card request");
        // Генерация номера карты и пин-кода
        BankCard card = cardService.createCard();
        // Возвращение сгенерированной карты в качестве ответа
        log.info("Generated card: {}", card);

        return new ResponseEntity<>(card, HttpStatus.OK);
    }

    @GetMapping("/card/{cardNumber}")
    public ResponseEntity<Optional<BankCard>> getCardByNumber(@PathVariable Long cardNumber) {
        Optional<BankCard> card = cardService.findByCardNumber(String.valueOf(cardNumber));
        if (card.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(card);
    }

    @PostMapping
    public ResponseEntity<BankCard> createCard(@RequestBody BankCard card) {
        log.info("Creating new card: {}", card);
        BankCard createdCard = cardService.saveCard(card);
        log.info("New card created: {}", createdCard);
        return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
    }


    @GetMapping("/cards")
    public ResponseEntity<List<BankCard>> getAllCards() {
        List<BankCard> cards = cardATMRepository.findAll();
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