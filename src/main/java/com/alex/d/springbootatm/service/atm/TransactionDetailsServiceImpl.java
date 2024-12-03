package com.alex.d.springbootatm.service.atm;

import com.alex.d.springbootatm.model.dto.TransactionDto;
import com.alex.d.springbootatm.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionDetailsServiceImpl implements TransactionDetailsService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public List<TransactionDto> getTransactionDetailsByCardNumber(String cardNumber) {
        return transactionRepository.findTransactionDetailsByCardNumber(cardNumber);
    }
}
