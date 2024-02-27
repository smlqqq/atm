package com.alex.d.springbootatm.repository;

import com.alex.d.springbootatm.model.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {

    BankCard findByCardNumber(String cardNum);


//    BankCard deleteByCardNumber(String cardNumber);
    void deleteByCardNumber(String cardNumber);
}
