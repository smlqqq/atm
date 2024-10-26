package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.dto.TransactionDetailsDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionService {
    List<TransactionDetailsDto> getTransactionDetailsByCardNumber(String cardNumber);
}
