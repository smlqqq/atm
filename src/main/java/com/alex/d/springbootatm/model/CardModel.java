package com.alex.d.springbootatm.model;


import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "atm_accounts")
@Schema(hidden = true)
public class CardModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "pin_number")
    private String pinNumber;

    @Column(name = "balance")
    private BigDecimal balance;

    @OneToMany(mappedBy = "senderCard") // Field senderCard in TransactionModel
    private Set<TransactionModel> sentTransactions;

    @OneToMany(mappedBy = "recipientCard") // Field recipientCard in TransactionModel
    private Set<TransactionModel> receivedTransactions;

    public CardModel(Long id, String cardNumber, String pinNumber, BigDecimal balance) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.pinNumber = pinNumber;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "CardModel{" +
                "cardNumber='" + cardNumber + '\'' +
                ", pinNumber='" + pinNumber + '\'' +
                ", balance=" + balance +
                '}';
    }
}

