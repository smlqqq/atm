package com.alex.d.springbootatm.repository;

import com.alex.d.springbootatm.dto.BankCardDTO;
import com.alex.d.springbootatm.model.BankCardModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


@Repository
public interface BankCardRepository extends JpaRepository<BankCardModel, Long> {

   Optional<BankCardModel> findByCardNumber(String cardNum);

}
