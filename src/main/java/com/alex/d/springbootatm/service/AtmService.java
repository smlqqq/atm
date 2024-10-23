package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.dto.BankCardDTO;
import com.alex.d.springbootatm.model.AtmModel;
import com.alex.d.springbootatm.model.BankCardModel;
import com.alex.d.springbootatm.response.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AtmService {

    TransferResponse sendTransaction(String senderCard, String recipientCard, BigDecimal amount);

    BankCardModel saveCreatedCardToDB();

    String generatePinCode();

    String generateCreditCardNumber();

    BigDecimal generateBalance();

    BalanceResponse checkBalanceByCardNumber(String cardNumber);

    AtmModel returnAtmName();

    String hashPinCode(String password);

    TransactionResponse processTransaction(String cardNumber, BigDecimal amount, boolean isDeposit);

}
