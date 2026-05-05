package com.example.school_management.exam.repository;

import com.example.school_management.exam.entity.EvaluationComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationComponentRepository extends JpaRepository<EvaluationComponent, Long> {}