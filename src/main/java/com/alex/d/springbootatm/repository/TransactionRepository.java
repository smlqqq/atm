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
            "a.senderCard.cardNumber, " +
            "a.senderBalanceAfter, " +
            "a.transactionType, " +
            "a.senderAtmModel.name, " +
            "a.recipientCard.cardNumber, " +
            "a.amount, " +
            "a.recipientBalanceAfter, " +
            "a.timestamp) " +
            "FROM TransactionModel a " +
            "WHERE a.senderCard.cardNumber = :cardNumber OR a.recipientCard.cardNumber = :cardNumber")
    List<TransactionDetailsDTO> findTransactionDetailsByCardNumber(@Param("cardNumber") String cardNumber);

}
