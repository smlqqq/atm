package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.dto.TransactionDto;
import com.alex.d.springbootatm.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public List<TransactionDto> getTransactionDetailsByCardNumber(String cardNumber) {
        return transactionRepository.findTransactionDetailsByCardNumber(cardNumber);
    }
}
