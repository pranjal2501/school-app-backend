package com.example.school_management.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByMobile(String mobile);
    /**
     * Finds an employee by checking if the generic identifier matches
     * either their email or their mobile number.
     */
    @Query("SELECT e FROM Employee e WHERE e.email = :identifier OR e.mobile = :identifier")
    Optional<Employee> findByIdentifier(@Param("identifier") String identifier);}