package com.alex.d.springbootatm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TransactionDetailsDto {

    private String sender;
    private BigDecimal senderBalance;
    private String transactionType;
    private String atmName;
    private String recipient;
    private BigDecimal amount;
    private BigDecimal recipientBalance;
    private LocalDateTime timestamp;

}
