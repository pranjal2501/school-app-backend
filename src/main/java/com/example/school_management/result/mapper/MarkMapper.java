package com.example.school_management.result.mapper;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.common.enums.ExamTerm;
import com.example.school_management.exam.entity.EvaluationComponent;
import com.example.school_management.exam.entity.ExamType;
import com.example.school_management.exam.entity.MarkingScheme;
import com.example.school_management.exam.entity.MarkingSchemeDetail;
import com.example.school_management.result.dto.ComponentMarkResponseDto;
import com.example.school_management.result.dto.MarkResponseDto;
import com.example.school_management.result.dto.StudentMarkEntryDto;
import com.example.school_management.result.entity.StudentSubjectMark;
import com.example.school_management.student.entity.Student;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MarkMapper {

    // Converts a new DTO into a brand new Entity
    public StudentSubjectMark toEntity(
            Integer obtainedMarks,
            Student student,
            ClassSubjectTeacher allocation,
            ExamType examType,
            EvaluationComponent component) {

        if (student == null || allocation == null || examType == null || component == null) {
            return null;
        }

        return StudentSubjectMark.builder()
                .student(student)
                .allocation(allocation)
                .examType(examType)
                .evaluationComponent(component)
                .obtainedMarks(obtainedMarks)
                .build();
    }

    // Mutates an existing entity (Upsert logic)
//    public void updateEntity(StudentMarkEntryDto dto, StudentSubjectMark existingMark) {
//        if (dto == null || existingMark == null) return;
//
//        existingMark.setTheoryObtained(dto.getTheoryObtained());
//        existingMark.setInternalObtained(dto.getInternalObtained());
//        existingMark.setPracticalObtained(dto.getPracticalObtained());
//        existingMark.setProjectObtained(dto.getProjectObtained());
//        existingMark.setOralObtained(dto.getOralObtained());
//        // Note: The @PreUpdate hook on the entity will automatically recalculate total and pass/fail!
//    }

    // Converts Entity to Response DTO
    // 2. Maps a list of component marks to the final dynamic MarkResponseDto
    public MarkResponseDto toResponseDto(Student student, List<StudentSubjectMark> studentMarks, MarkingScheme scheme) {
        if (student == null || scheme == null) {
            return null;
        }

        int totalObtained = 0;
        int totalMax = 0;
        boolean isPassed = true;

        List<ComponentMarkResponseDto> compResponses = new ArrayList<>();

        // Loop through the Marking Scheme to build the dynamic list of components
        for (MarkingSchemeDetail detail : scheme.getDetails()) {
            Long compId = detail.getEvaluationComponent().getId();

            // Find the mark for this specific component
            StudentSubjectMark mark = studentMarks.stream()
                    .filter(m -> m.getEvaluationComponent().getId().equals(compId))
                    .findFirst()
                    .orElse(null);

            // Default to 0 if the mark record doesn't exist yet
            int obtained = (mark != null && mark.getObtainedMarks() != null) ? mark.getObtainedMarks() : 0;
            boolean compPassed = obtained >= detail.getPassingMarks();

            if (!compPassed) {
                isPassed = false;
            }

            totalObtained += obtained;
            totalMax += detail.getMaxMarks();

            compResponses.add(ComponentMarkResponseDto.builder()
                    .markRecordId(mark != null ? mark.getId() : null)
                    .evaluationComponentId(compId)
                    .componentName(detail.getEvaluationComponent().getName())
                    .maxMarks(detail.getMaxMarks())
                    .passingMarks(detail.getPassingMarks())
                    .obtainedMarks(obtained)
                    .isComponentPassed(compPassed)
                    .build());
        }

        // Build and return the consolidated DTO
        return MarkResponseDto.builder()
                .studentId(student.getId())
                .studentName(student.getFirstName() + " " + student.getLastName())
                .rollNumber(student.getRollNumber())
                .examTypeId(scheme.getExamType().getId())
                .examTypeName(scheme.getExamType().getName())
                .componentMarks(compResponses)
                .totalObtained(totalObtained)
                .totalMaxMarks(totalMax)
                .isPassed(isPassed)
                .build();
    }
}