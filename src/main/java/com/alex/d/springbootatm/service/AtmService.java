package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.model.AtmModel;
import com.alex.d.springbootatm.model.CardModel;
import com.alex.d.springbootatm.response.BalanceResponse;
import com.alex.d.springbootatm.response.TransactionResponse;
import com.alex.d.springbootatm.response.TransferResponse;

import java.math.BigDecimal;

public interface AtmService {

    TransferResponse transferBetweenCards(String senderCard, String recipientCard, BigDecimal amount);

    BalanceResponse checkBalanceByCardNumber(String cardNumber);

    AtmModel returnAtmName();

    TransactionResponse updateAccountBalance(String cardNumber, BigDecimal amount, boolean isDeposit);

    CardModel fetchCardModel(String card);

}
