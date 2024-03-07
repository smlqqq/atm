package com.alex.d.springbootatm.repository;

import com.alex.d.springbootatm.model.BankCardModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface BankCardRepository extends JpaRepository<BankCardModel, Long> {

    BankCardModel findByCardNumber(String cardNum);


//    BankCard deleteByCardNumber(String cardNumber);
    void deleteByCardNumber(String cardNumber);
}
