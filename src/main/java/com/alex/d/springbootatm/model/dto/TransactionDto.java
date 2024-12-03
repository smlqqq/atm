package com.alex.d.springbootatm.model.dto;

import com.alex.d.springbootatm.model.AtmModel;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Builder
public class TransactionDto {

    String sender;
    BigDecimal senderBalance;
    String transactionType;
    String atmName;
    String recipient;
    BigDecimal amount;
    BigDecimal recipientBalance;
    LocalDateTime timestamp;


}
