package com.alex.d.springbootatm.service.atm;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionType {

    SEND("SEND"),
    WITHDRAW("WITHDRAW"),
    DEPOSIT("DEPOSIT"),
    RECEIVE("RECEIVE_FROM");

    private final String transactionType;

}
