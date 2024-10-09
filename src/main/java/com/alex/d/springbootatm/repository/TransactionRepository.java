package com.alex.d.springbootatm.repository;

import com.alex.d.springbootatm.dto.TransactionDetailsDTO;
import com.alex.d.springbootatm.model.TransactionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionModel, Long> {

    @Query("SELECT new com.alex.d.springbootatm.dto.TransactionDetailsDTO(" +
            "acc.cardNumber, a.amount, a.senderCard.cardNumber, a.transactionType, a.recipientCard.cardNumber, " +
            "acc.balance, a.timestamp) " +
            "FROM TransactionModel a " +
            "JOIN a.recipientCard acc " +
            "WHERE acc.cardNumber = :cardNumber")
    List<TransactionDetailsDTO> findTransactionDetailsByCardNumber(@Param("cardNumber") String cardNumber);

}
