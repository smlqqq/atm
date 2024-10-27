package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.dto.CardDto;
import com.alex.d.springbootatm.model.CardModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface ManagerService {

    List<CardModel> getAllCards();

    CardModel deleteCardByNumber(String cardNumber);

    CardDto createCard();

    String generatePinCode();

    String generateCreditCardNumber();

    BigDecimal generateBalance();

    CardModel saveCreatedCardToDB();

    String hashPinCode(String pinCode);

}
