package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.dto.BankCardDTO;
import com.alex.d.springbootatm.model.BankCardModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ManagerService {

    Optional<List<BankCardModel>> getAllCards();

    BankCardModel deleteCardByNumber(String cardNumber);

    BankCardDTO createCard();
}
