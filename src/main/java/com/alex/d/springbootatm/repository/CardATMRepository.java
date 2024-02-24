package com.alex.d.springbootatm.repository;

import com.alex.d.springbootatm.model.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CardATMRepository extends JpaRepository<BankCard, Long> {

    Optional<BankCard> findByCardNumber(String cardNum);


    Optional<BankCard> deleteByCardNumber(String cardNumber);
}
