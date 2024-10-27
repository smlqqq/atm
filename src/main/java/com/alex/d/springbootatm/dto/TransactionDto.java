package com.alex.d.springbootatm.dto;

import com.alex.d.springbootatm.model.AtmModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TransactionDto {

    private String sender;
    private BigDecimal senderBalance;
    private String transactionType;
    private String atmName;
    private String recipient;
    private BigDecimal amount;
    private BigDecimal recipientBalance;
    private LocalDateTime timestamp;


}
