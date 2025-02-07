package com.alex.d.springbootatm.service.card;

import com.alex.d.springbootatm.model.BankCard;

import java.math.BigDecimal;

public interface BankCardGenerationService {

    BankCard buildCardModel(String pin);

//    String passwordEncode(String pinCode);

//    String generatePinCode();

    char[] pinCodeGenerator();

    String bankCardNumGenerator();

    BigDecimal generateBalance();

}
