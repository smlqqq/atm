package com.alex.d.springbootatm.repository;

import com.alex.d.springbootatm.model.CardATM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface CardATMRepository extends JpaRepository<CardATM, Long> {


    //    Optional<CardATM> findByCardNumber(String cardNum);
    CardATM findByCardNumber(String cardNum);

    //    CardATM findByBalanceOrderByCardNumber(BigDecimal balance);
    List<CardATM> findByBalanceOrderByCardNumber(BigDecimal balance);

}
