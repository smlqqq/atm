package com.alex.d.springbootatm.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "atm_transactions")
@JsonTypeName("Transaction")
public class TransactionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "timestamp", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender", referencedColumnName = "card_number")
    private BankCardModel senderCard;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipient", referencedColumnName = "card_number")
    private BankCardModel recipientCard;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "atm_name", referencedColumnName = "name")
    private ATMModel senderATMModel;

    @ManyToOne(fetch = FetchType.EAGER)
    private BankCardModel bankCardModel;

}
