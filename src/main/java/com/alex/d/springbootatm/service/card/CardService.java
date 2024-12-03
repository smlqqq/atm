package com.alex.d.springbootatm.service.card;

import com.alex.d.springbootatm.model.CardModel;
import com.alex.d.springbootatm.model.dto.CardDto;

import java.util.List;

public interface CardService {

    List<CardDto> getAllCards();

    CardDto deleteCardByNumber(String cardNumber);

    CardDto createAndSaveCard();

    CardDto saveCreatedCardToDB(CardModel card);

}
