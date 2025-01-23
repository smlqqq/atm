package com.alex.d.springbootatm.repository;

import com.alex.d.springbootatm.model.BankCardTransaction;
import com.alex.d.springbootatm.dto.BankCardTransactionDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<BankCardTransaction, Long> {

    @Query("SELECT new com.alex.d.springbootatm.dto.BankCardTransactionDto(" +
            "a.senderCard.cardNumber, " +
            "a.senderBalanceAfter, " +
            "a.transactionType, " +
            "a.senderAtm.name, " +
            "a.recipientCard.cardNumber, " +
            "a.amount, " +
            "a.recipientBalanceAfter, " +
            "a.timestamp) " +
            "FROM  BankCardTransaction a " +
            "WHERE a.senderCard.cardNumber = :cardNumber OR a.recipientCard.cardNumber = :cardNumber")
    List<BankCardTransactionDto> findTransactionDetailsByCardNumber(@Param("cardNumber") String cardNumber);

}
