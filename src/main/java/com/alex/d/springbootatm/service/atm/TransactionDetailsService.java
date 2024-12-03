package com.alex.d.springbootatm.service.atm;

import com.alex.d.springbootatm.model.dto.TransactionDto;

import java.util.List;

public interface TransactionDetailsService {
    List<TransactionDto> getTransactionDetailsByCardNumber(String cardNumber);
}
