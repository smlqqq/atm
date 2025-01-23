package com.alex.d.springbootatm.repository;

import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.dto.BankCardDto;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;


@Repository
public interface CardRepository extends JpaRepository<BankCard, Long> {

   Optional<BankCard> findByCardNumber(String cardNum);

   @Query("SELECT new com.alex.d.springbootatm.dto.BankCardDto(c.cardNumber, c.pinNumber, c.balance) " +
           "FROM BankCard c " +
           "WHERE c.cardNumber = :cardNum")
   BankCardDto getAccountDetailsByCardNumber(@Param("cardNum") String cardNum);

   @Modifying
   @Transactional
   @Query("UPDATE BankCard c SET c.balance = c.balance + :amount WHERE c.cardNumber = :cardNumber")
   void addBalance(@Param("cardNumber") String cardNumber, @Param("amount") BigDecimal amount);

   @Modifying
   @Transactional
   @Query("UPDATE BankCard c SET c.balance = c.balance - :amount WHERE c.cardNumber = :cardNumber")
   void subtractBalance(@Param("cardNumber") String cardNumber, @Param("amount") BigDecimal amount);

}
