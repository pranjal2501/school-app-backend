package com.example.school_management.finance.repository;

import com.example.school_management.finance.entity.FinanceOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinanceOfficerRepository extends JpaRepository<FinanceOfficer, Long> {

    Optional<FinanceOfficer> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByMobile(String mobile);
    boolean existsByAadharNo(String aadharNo);

    Optional<FinanceOfficer> findByMobile(String identifier);
}