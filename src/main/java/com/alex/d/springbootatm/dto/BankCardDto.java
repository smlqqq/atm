package com.alex.d.springbootatm.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BankCardDto(
        String cardNumber, BigDecimal balance) {
}
