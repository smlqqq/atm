package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.model.AtmModel;
import com.alex.d.springbootatm.model.BankCardModel;
import com.alex.d.springbootatm.response.BalanceResponse;
import com.alex.d.springbootatm.response.TransactionResponse;
import com.alex.d.springbootatm.response.TransferResponse;

import java.math.BigDecimal;

public interface AtmService {

    TransferResponse sendTransaction(String senderCard, String recipientCard, BigDecimal amount);

    BalanceResponse checkBalanceByCardNumber(String cardNumber);

    AtmModel returnAtmName();

    TransactionResponse processTransaction(String cardNumber, BigDecimal amount, boolean isDeposit);

    BankCardModel fetchCardModel(String card);

    String fetchCardNumberAsString(String card);

}
