package com.alex.d.springbootatm.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDto {

    private String cardNumber;
    private String pinCode;
    private BigDecimal balance;

}

