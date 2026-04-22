package com.example.school_management.studyMaterial.repository;

import com.example.school_management.studyMaterial.entity.StudyMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, Long> {
    List<StudyMaterial> findByClassTeacherId(Long classSubjectTeacherId);
}
