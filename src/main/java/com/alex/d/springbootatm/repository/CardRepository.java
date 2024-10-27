package com.alex.d.springbootatm.repository;

import com.alex.d.springbootatm.dto.CardDto;
import com.alex.d.springbootatm.model.CardModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Repository
public interface CardRepository extends JpaRepository<CardModel, Long> {

   Optional<CardModel> findByCardNumber(String cardNum);


   @Query("SELECT new com.alex.d.springbootatm.dto.CardDto(c.balance) " +
           "FROM CardModel c " +
           "WHERE c.cardNumber = :cardNum")
   CardDto getBankCardBalanceByCardNumber(@Param("cardNum") String cardNum);

   @Query("SELECT new com.alex.d.springbootatm.dto.CardDto(c.cardNumber, c.pinNumber, c.balance) " +
           "FROM CardModel c " +
           "WHERE c.cardNumber = :cardNum")
   CardDto getBankCardDetailsByCardNumber(@Param("cardNum") String cardNum);

   @Modifying
   @Transactional
   @Query("UPDATE CardModel c SET c.balance = c.balance + :amount WHERE c.cardNumber = :cardNumber")
   void addBalance(@Param("cardNumber") String cardNumber, @Param("amount") BigDecimal amount);

   @Modifying
   @Transactional
   @Query("UPDATE CardModel c SET c.balance = c.balance - :amount WHERE c.cardNumber = :cardNumber")
   void subtractBalance(@Param("cardNumber") String cardNumber, @Param("amount") BigDecimal amount);

}
