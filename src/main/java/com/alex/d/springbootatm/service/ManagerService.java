package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.dto.CardDto;
import com.alex.d.springbootatm.model.BankCardModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public interface ManagerService {

    List<BankCardModel> getAllCards();

    BankCardModel deleteCardByNumber(String cardNumber);

    CardDto createCard();

    String generatePinCode();

    String generateCreditCardNumber();

    BigDecimal generateBalance();

    BankCardModel saveCreatedCardToDB();

    String hashPinCode(String pinCode);

}
