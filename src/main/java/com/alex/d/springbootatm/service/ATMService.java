package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.dto.BankCardDTO;
import com.alex.d.springbootatm.model.AtmModel;
import com.alex.d.springbootatm.model.BankCardModel;
import com.alex.d.springbootatm.response.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ATMService {

    TransferResponse sendTransaction(String senderCard, String recipientCard, BigDecimal amount);

    BankCardDTO createCard();

    BankCardModel saveCreatedCardToDB();

    String generatePinCode();

    String generateCreditCardNumber();

    BigDecimal generateBalance();

    BalanceResponse checkBalanceByCardNumber(String cardNumber);

    BankCardModel deleteCardByNumber(String cardNumber);

    AtmModel returnAtmName();

    String hashPinCode(String password);

    Optional<List<BankCardModel>> getAllCards();

    TransactionResponse processTransaction(String cardNumber, BigDecimal amount, boolean isDeposit);

}
