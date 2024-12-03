package com.alex.d.springbootatm.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "atm_transactions")
@Schema(hidden = true)
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
    @JsonBackReference
    @JoinColumn(name = "sender", referencedColumnName = "card_number")
    private CardModel senderCard;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    @JoinColumn(name = "recipient", referencedColumnName = "card_number")
    private CardModel recipientCard;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "atm_name", referencedColumnName = "name")
    private AtmModel senderAtmModel;

    @Column(name = "sender_balance", nullable = true)
    private BigDecimal senderBalanceAfter;

    @Column(name = "recipient_balance", nullable = true)
    private BigDecimal recipientBalanceAfter;

}
