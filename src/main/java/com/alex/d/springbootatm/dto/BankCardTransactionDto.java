package com.alex.d.springbootatm.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record BankCardTransactionDto(
        String sender,
        BigDecimal senderBalance,
        String transactionType,
        String atmName,
        String recipient,
        BigDecimal amount,
        BigDecimal recipientBalance,
        LocalDateTime timestamp) {
}
