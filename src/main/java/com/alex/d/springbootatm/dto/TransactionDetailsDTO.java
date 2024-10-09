package com.alex.d.springbootatm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TransactionDetailsDTO {

    private String cardNumber;
    private BigDecimal amount;
    private String sender;
    private String transactionType;
    private String recipient;
    private BigDecimal balance;
    private LocalDateTime timestamp;

}
