package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.dto.BankCardDTO;
import com.alex.d.springbootatm.model.ATMModel;
import com.alex.d.springbootatm.model.BankCardModel;

import java.math.BigDecimal;
import java.util.Optional;

public interface ATMService {

    void sendTransaction(Optional<BankCardModel> senderCard, Optional<BankCardModel> recipientCard, BigDecimal amount);

    void depositCashFromATM(Optional<BankCardModel> card, BigDecimal amount);

    void withdrawFromATM(Optional<BankCardModel> card, BigDecimal amount);

    BankCardDTO createCard();

    BankCardModel saveCreatedCardToDB();

    String generatePinCode();

    String generateCreditCardNumber();

    BigDecimal generateBalance();

    BigDecimal checkBalanceByCardNumber(String cardNumber);

    Optional<BankCardModel> deleteCardByNumber(String cardNumber);

    ATMModel returnAtmName();

    String hashPinCode(String password);

}
