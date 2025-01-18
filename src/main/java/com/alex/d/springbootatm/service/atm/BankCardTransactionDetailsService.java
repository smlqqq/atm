package com.alex.d.springbootatm.service.atm;

import com.alex.d.springbootatm.dto.BankCardTransactionDto;

import java.util.List;

public interface BankCardTransactionDetailsService {
    List<BankCardTransactionDto> getTransactionDetailsByCardNumber(String cardNumber);
}
