package com.example.school_management.studyMaterial.repository;

import com.example.school_management.studyMaterial.entity.StudyMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, Long> {
    List<StudyMaterial> findByClassTeachers_Id(Long classSubjectTeacherId);
    @Query("SELECT DISTINCT sm FROM StudyMaterial sm JOIN sm.classTeachers ct WHERE ct.schoolClass.id = :classId AND ct.subject.id = :subjectId")
    List<StudyMaterial> findByClassIdAndSubjectId(@Param("classId") Long classId, @Param("subjectId") Long subjectId);
}
