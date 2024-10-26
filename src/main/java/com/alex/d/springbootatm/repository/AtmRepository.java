package com.alex.d.springbootatm.repository;

import com.alex.d.springbootatm.model.AtmModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtmRepository extends JpaRepository<AtmModel, Long> {

}
