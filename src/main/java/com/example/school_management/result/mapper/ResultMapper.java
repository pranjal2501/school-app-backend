package com.example.school_management.result.mapper;

import com.example.school_management.exam.entity.MarkingScheme;
import com.example.school_management.exam.entity.MarkingSchemeDetail;
import com.example.school_management.result.dto.resultDisplay.ComponentResultDto;
import com.example.school_management.result.dto.resultDisplay.LetterHeadResponseDto;
import com.example.school_management.result.dto.resultDisplay.StudentResultResponseDto;
import com.example.school_management.result.dto.resultDisplay.SubjectMarkDto;
import com.example.school_management.result.entity.ResultLetterhead;
import com.example.school_management.result.entity.StudentSubjectMark;
import com.example.school_management.student.entity.Student;
import com.example.school_management.subject.entity.Subject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ResultMapper {

    public SubjectMarkDto toSubjectMarkDto(Subject subject, MarkingScheme scheme, List<StudentSubjectMark> componentMarks) {

        int totalMax = 0;
        int totalPassing = 0;
        int totalObtained = 0;
        boolean isOverallPassed = true;

        List<ComponentResultDto> componentDtos = new ArrayList<>();

        for (MarkingSchemeDetail detail : scheme.getDetails()) {
            Long compId = detail.getEvaluationComponent().getId();

            StudentSubjectMark mark = componentMarks.stream()
                    .filter(m -> m.getEvaluationComponent().getId().equals(compId))
                    .findFirst()
                    .orElse(null);

            int obtained = (mark != null && mark.getObtainedMarks() != null) ? mark.getObtainedMarks() : 0;
            boolean isComponentPassed = obtained >= detail.getPassingMarks();

            if (!isComponentPassed) {
                isOverallPassed = false;
            }

            totalMax += detail.getMaxMarks();
            totalPassing += detail.getPassingMarks();
            totalObtained += obtained;

            componentDtos.add(ComponentResultDto.builder()
                    .componentName(detail.getEvaluationComponent().getName())
                    .maxMarks(detail.getMaxMarks())
                    .passingMarks(detail.getPassingMarks())
                    .obtainedMarks(obtained)
                    .isPassed(isComponentPassed)
                    .build());
        }

        return SubjectMarkDto.builder()
                .subjectName(subject.getSubjectName())
                .subjectCode(subject.getSubjectCode())
                .examTypeName(scheme.getExamType().getName())
                .components(componentDtos)
                .totalMax(totalMax)
                .totalPassingMarks(totalPassing)
                .totalObtained(totalObtained)
                .isPassed(isOverallPassed)
                .build();
    }

    public StudentResultResponseDto toResultResponseDto(
            Student student,
            List<SubjectMarkDto> subjectMarks,
            String examTypeName,
            String academicYear,
            ResultLetterhead letterhead) {

        int grandTotalObtained = 0;
        int grandTotalMax = 0;
        boolean isOverallPassed = true;

        for (SubjectMarkDto subject : subjectMarks) {
            grandTotalObtained += (subject.getTotalObtained() != null) ? subject.getTotalObtained() : 0;
            grandTotalMax += (subject.getTotalMax() != null) ? subject.getTotalMax() : 0;

            if (Boolean.FALSE.equals(subject.getIsPassed())) {
                isOverallPassed = false;
            }
        }

        float percentage = 0.0f;
        if (grandTotalMax > 0) {
            percentage = ((float) grandTotalObtained / grandTotalMax) * 100;
        }

        return StudentResultResponseDto.builder()
                .studentName(student.getFirstName() + " " + student.getLastName())
                .registrationId(student.getRegId())
                .rollNumber(student.getRollNumber())
                .className(student.getAssignedClass().getClassName())
                .examTypeName(examTypeName)
                .academicYear(academicYear)
                .subjects(subjectMarks)
                .letterheadUrl(letterhead != null ? letterhead.getUrl() : null)
                .grandTotalObtained(grandTotalObtained)
                .grandTotalMax(grandTotalMax)
                .percentage(percentage)
                .isOverallPassed(isOverallPassed)
                .build();
    }

    public LetterHeadResponseDto toLetterheadResponse(ResultLetterhead entity) {
        if (entity == null) {
            return null;
        }

        return LetterHeadResponseDto.builder()
                .url(entity.getUrl())
                .examTypeId(entity.getExamTypeId())
                .academicYear(entity.getAcademicYear())
                .build();
    }
}