package com.alex.d.springbootatm.model;


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
public class BankCard {
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

    @OneToMany(mappedBy = "senderCard") // Field senderCard in BankCardTransaction
    private Set<BankCardTransaction> sentTransactions;

    @OneToMany(mappedBy = "recipientCard") // Field recipientCard in BankCardTransaction
    private Set<BankCardTransaction> receivedTransactions;

    public BankCard(Long id, String cardNumber, String pinNumber, BigDecimal balance) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.pinNumber = pinNumber;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "BankCard{" +
                "cardNumber='" + cardNumber + '\'' +
                ", pinNumber='" + pinNumber + '\'' +
                ", balance=" + balance +
                '}';
    }
}

