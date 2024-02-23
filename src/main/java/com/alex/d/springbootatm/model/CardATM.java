package com.alex.d.springbootatm.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "atm_accounts")
public class CardATM {
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

//    @OneToMany(mappedBy = "senderCard", fetch = FetchType.LAZY)
//    private List<TransactionATM> sentTransactions;
//
//    @OneToMany(mappedBy = "recipientCard", fetch = FetchType.LAZY)
//    private List<TransactionATM> receivedTransactions;

}

