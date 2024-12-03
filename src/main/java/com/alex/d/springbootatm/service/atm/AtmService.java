package com.alex.d.springbootatm.service.atm;

import com.alex.d.springbootatm.model.AtmModel;
import com.alex.d.springbootatm.model.CardModel;
import com.alex.d.springbootatm.model.dto.response.BalanceResponse;
import com.alex.d.springbootatm.model.dto.response.CardResponse;
import com.alex.d.springbootatm.model.dto.response.TransactionResponse;

import java.math.BigDecimal;

public interface AtmService {

    TransactionResponse transferBetweenCards(String senderCard, String recipientCard, BigDecimal amount);

    BalanceResponse checkBalanceByCardNumber(String cardNumber);

    AtmModel returnAtmName();

    CardResponse updateAccountBalance(String cardNumber, BigDecimal amount, boolean isDeposit);

    CardModel fetchCardFromDb(String card);

    void addAmountToBalance(String card, BigDecimal amount);

    void subtractAmountFromBalance(String card, BigDecimal amount);

    BigDecimal addOrSubtractBalance(String cardNumber, BigDecimal amount, boolean addAmount);

}
