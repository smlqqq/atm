package com.alex.d.springbootatm.service.card;

import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.dto.BankCardDto;

import java.util.List;

public interface BankCardService {

    List<BankCardDto> getAllCards();

    BankCardDto deleteCardByNumber(String cardNumber);

    BankCardDto createCard();

    BankCardDto saveCardToDB(BankCard card);

}
