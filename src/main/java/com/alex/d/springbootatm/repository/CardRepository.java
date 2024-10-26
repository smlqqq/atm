package com.alex.d.springbootatm.repository;

import com.alex.d.springbootatm.dto.CardDto;
import com.alex.d.springbootatm.model.BankCardModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;


@Repository
public interface CardRepository extends JpaRepository<BankCardModel, Long> {

   Optional<BankCardModel> findByCardNumber(String cardNum);

   @Query("SELECT new com.alex.d.springbootatm.dto.CardDto(c.balance) " +
           "FROM BankCardModel c " +
           "WHERE c.cardNumber = :cardNum")
   CardDto getBankCardBalanceByCardNumber(@Param("cardNum") String cardNum);


   @Query("SELECT new com.alex.d.springbootatm.dto.CardDto(c.cardNumber, c.pinNumber, c.balance) " +
           "FROM BankCardModel c " +
           "WHERE c.cardNumber = :cardNum")
   CardDto getBankCardDetailsByCardNumber(@Param("cardNum") String cardNum);

   @Modifying
   @Transactional
   @Query("UPDATE BankCardModel c SET c.balance = c.balance + :amount WHERE c.cardNumber = :cardNumber")
   BigDecimal addBalance(@Param("cardNumber") String cardNumber, @Param("amount") BigDecimal amount);

   @Modifying
   @Transactional
   @Query("UPDATE BankCardModel c SET c.balance = c.balance - :amount WHERE c.cardNumber = :cardNumber")
   BigDecimal subtractBalance(@Param("cardNumber") String cardNumber, @Param("amount") BigDecimal amount);


}
