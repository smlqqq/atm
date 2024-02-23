package com.alex.d.springbootatm.repository;

import com.alex.d.springbootatm.model.ATM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ATMRepository extends JpaRepository<ATM, Long> {

}
