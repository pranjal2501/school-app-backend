package com.example.school_management.fees.repository;

import com.example.school_management.common.enums.Standard;
import com.example.school_management.fees.entity.Fees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeesRepository extends JpaRepository<Fees, Long>
{
    List<Fees> findByStd(Standard std);;
    List<Fees> findByAcademicYear(String academicYear);
    List<Fees> findByStdAndAcademicYear(Standard std, String academicYear);
}