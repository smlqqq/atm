package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.dto.TransactionDetailsDTO;
import com.alex.d.springbootatm.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public List<TransactionDetailsDTO> getTransactionDetailsByCardNumber(String cardNumber) {
        return transactionRepository.findTransactionDetailsByCardNumber(cardNumber);
    }
}
