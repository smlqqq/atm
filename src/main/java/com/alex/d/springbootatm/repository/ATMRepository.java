package com.alex.d.springbootatm.repository;

import com.alex.d.springbootatm.model.ATMModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ATMRepository extends JpaRepository<ATMModel, Long> {

}
