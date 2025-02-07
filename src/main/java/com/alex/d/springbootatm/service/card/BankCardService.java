package com.alex.d.springbootatm.service.card;

import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.dto.BankCardDto;

import java.util.List;

public interface BankCardService {

    List<BankCardDto> fetchAllBankCardsData();

    BankCardDto deleteBankCardByNumber(String cardNumber);

    BankCardDto createBankCard();

    BankCardDto saveBankCardToDB(BankCard card);

    BankCard fetchCardFromDb(String card);

}
