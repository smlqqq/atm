package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.dto.TransactionDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionService {
    List<TransactionDto> getTransactionDetailsByCardNumber(String cardNumber);
}
