package com.alex.d.springbootatm.repository;

import com.alex.d.springbootatm.model.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepo extends JpaRepository<Transactions, Long> {
}
