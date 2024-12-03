package com.alex.d.springbootatm.service.atm;

import com.alex.d.springbootatm.model.AtmModel;
import com.alex.d.springbootatm.model.CardModel;
import com.alex.d.springbootatm.model.response.BalanceResponse;
import com.alex.d.springbootatm.model.response.DepositeResponse;
import com.alex.d.springbootatm.model.response.TransferResponse;

import java.math.BigDecimal;

public interface AtmService {

    TransferResponse transferBetweenCards(String senderCard, String recipientCard, BigDecimal amount);

    BalanceResponse checkBalanceByCardNumber(String cardNumber);

    AtmModel returnAtmName();

    DepositeResponse updateAccountBalance(String cardNumber, BigDecimal amount, boolean isDeposit);

//    CardModel fetchCardFromDb(String card);
    CardModel fetchCardFromDb(String card);

}
