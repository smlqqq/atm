package com.alex.d.springbootatm.service.atm;

import com.alex.d.springbootatm.dto.BankCardTransactionDto;
import com.alex.d.springbootatm.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankCardTransactionDetailsServiceImpl implements BankCardTransactionDetailsService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public List<BankCardTransactionDto> getTransactionDetailsByCardNumber(String cardNumber) {
        return transactionRepository.findTransactionDetailsByCardNumber(cardNumber);
    }
}
