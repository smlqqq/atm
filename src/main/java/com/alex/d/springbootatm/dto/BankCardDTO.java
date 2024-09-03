package com.alex.d.springbootatm.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BankCardDTO {
    private String cardNumber;
    private String plainPin; // Оригинальный 4-значный PIN-код
    private BigDecimal balance;
}
