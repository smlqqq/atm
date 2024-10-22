package com.alex.d.springbootatm.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankCardDTO {

    private String cardNumber;
    private String pinCode;
    private BigDecimal balance;


    public BankCardDTO(String cardNumber, BigDecimal balance) {
        this.cardNumber = cardNumber;
        this.balance = balance;
    }


}

