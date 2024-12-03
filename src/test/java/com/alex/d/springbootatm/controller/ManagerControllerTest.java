package com.alex.d.springbootatm.controller;

import com.alex.d.springbootatm.model.CardModel;
import com.alex.d.springbootatm.model.dto.CardDto;
import com.alex.d.springbootatm.repository.CardRepository;
import com.alex.d.springbootatm.service.card.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ManagerControllerTest {

    @Mock
    CardRepository bankCardRepository;

    @Mock
    CardService cardService;

    @InjectMocks
    ManagerController managerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllBankCards() {
        List<CardModel> cards = new ArrayList<>();
        cards.add(new CardModel(1L, "4000003813378680", "5356", BigDecimal.valueOf(300)));
        cards.add(new CardModel(2L, "4000007329214081", "3256", BigDecimal.valueOf(500)));

        List<CardDto> cardDtos = new ArrayList<>();

        for (CardModel card : cards) {
            CardDto cardDto = CardDto.builder()
                    .cardNumber(card.getCardNumber())
                    .pin(card.getPinNumber())
                    .balance(card.getBalance())
                    .build();
            cardDtos.add(cardDto);
        }

        when(cardService.getAllCards()).thenReturn(cardDtos);
        List<CardDto> retrievedCards = cardService.getAllCards();

        assertNotNull(retrievedCards);
        assertEquals(2, retrievedCards.size());
        assertEquals("4000003813378680", retrievedCards.get(0).getCardNumber());
        assertEquals("4000007329214081", retrievedCards.get(1).getCardNumber());
    }

    @Test
    void deleteCard() {
        String cardNumber = "4000007329214081";
        CardModel bankCard = new CardModel(1L, cardNumber, "5356", BigDecimal.valueOf(300));
        CardDto dto = CardDto.builder()
                        .cardNumber(bankCard.getCardNumber())
                                .pin(bankCard.getPinNumber())
                                        .balance(bankCard.getBalance())
                                                .build();

        when(bankCardRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(bankCard));
        when(cardService.deleteCardByNumber(cardNumber)).thenReturn(dto);

        ResponseEntity<?> response = managerController.delete(cardNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createNewCard() {
        CardModel bankCard = new CardModel(1L,"4000003813378680", "3256", BigDecimal.valueOf(0));
        CardDto dto = CardDto.builder()
                .cardNumber(bankCard.getCardNumber())
                .pin(bankCard.getPinNumber())
                .balance(bankCard.getBalance())
                .build();


        when(cardService.createCard()).thenReturn(dto);

        ResponseEntity<CardDto> response = managerController.create();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(dto, response.getBody());

    }
}
