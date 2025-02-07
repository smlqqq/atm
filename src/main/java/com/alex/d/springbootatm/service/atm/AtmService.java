package com.alex.d.springbootatm.service.atm;

import com.alex.d.springbootatm.model.Atm;
import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.model.response.*;

import java.math.BigDecimal;

public interface AtmService {

    TransactionResponse processCardTransaction(String senderCard, String recipientCard, BigDecimal amount);

    BalanceResponse checkBalanceByCardNumber(String cardNumber);

    Atm returnAtmName();

    CardResponse processAtmTransaction(String cardNumber, BigDecimal amount, String transactionType);

    DepositeResponse processDeposit(String cardNumber, BigDecimal amount);

    WithdrawResponse processWithdrawal(String cardNumber, BigDecimal amount);

    BigDecimal addAmountToBankCardBalance(String card, BigDecimal amount);

    BigDecimal subtractAmountFromBankCardBalance(String card, BigDecimal amount);

    BigDecimal addOrSubtractAmountFromBalance(String cardNumber, BigDecimal amount, boolean addAmount);

}
