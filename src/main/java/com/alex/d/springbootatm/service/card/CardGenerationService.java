package com.alex.d.springbootatm.service.card;

import com.alex.d.springbootatm.model.CardModel;

import java.math.BigDecimal;

public interface CardGenerationService {

    CardModel buildCardModel(String pin);

    String hashPinCode(String pinCode);

    String generatePinCode();

    String generateCreditCardNumber();

    BigDecimal generateBalance();

}
