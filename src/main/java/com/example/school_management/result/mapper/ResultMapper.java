package com.example.school_management.result.mapper;

import com.example.school_management.common.enums.ExamTerm;
import com.example.school_management.result.dto.resultDisplay.LetterHeadResponseDto;
import com.example.school_management.result.dto.resultDisplay.StudentResultResponseDto;
import com.example.school_management.result.dto.resultDisplay.SubjectMarkDto;
import com.example.school_management.result.entity.ResultLetterhead;
import com.example.school_management.result.entity.StudentSubjectMark;
import com.example.school_management.student.entity.Student;
import com.example.school_management.subject.entity.Subject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResultMapper {

    //===================== LETTER HEAD CONCERN =====================

    public LetterHeadResponseDto toLetterheadResponse(ResultLetterhead resultLetterhead)
    {
        LetterHeadResponseDto dto= new LetterHeadResponseDto();
        dto.setUrl(resultLetterhead.getUrl());
        dto.setExamTerm(resultLetterhead.getExamTerm());
        dto.setAcademicYear(resultLetterhead.getAcademicYear());

        return dto;
    }

    //===================== RESULT CONCERN ===========================

    public SubjectMarkDto toSubjectMarkDto(StudentSubjectMark mark) {
        Subject subject = mark.getAllocation().getSubject();

        return SubjectMarkDto.builder()
                .subjectName(subject.getSubjectName())
                .subjectCode(subject.getSubjectCode())
                .theoryMax(subject.getTheoryMarks())
                .internalMax(subject.getInternalMarks())
                .practicalMax(subject.getPracticalMarks())
                .projectMax(subject.getProjectMarks())
                .oralMax(subject.getOralMarks())
                .totalMax(subject.getTotalMarks())
                .passingMarks(subject.getPassingMarks())
                .theoryObtained(mark.getTheoryObtained())
                .internalObtained(mark.getInternalObtained())
                .practicalObtained(mark.getPracticalObtained())
                .projectObtained(mark.getProjectObtained())
                .oralObtained(mark.getOralObtained())
                .totalObtained(mark.getTotalObtained())
                .isPassed(mark.isPassed())
                .build();
    }

    public StudentResultResponseDto toResultResponseDto(
            Student student,
            List<StudentSubjectMark> marks,
            List<SubjectMarkDto> subjectMarkDtos,
            ExamTerm examTerm,
            String academicYear,
            ResultLetterhead letterhead) {

        int grandTotalObtained = marks.stream()
                .mapToInt(m -> m.getTotalObtained() != null ? m.getTotalObtained() : 0)
                .sum();

        int grandTotalMax = marks.stream()
                .mapToInt(m -> m.getAllocation().getSubject().getTotalMarks() != null
                        ? m.getAllocation().getSubject().getTotalMarks() : 0)
                .sum();

        float percentage = grandTotalMax > 0
                ? (grandTotalObtained * 100.0f) / grandTotalMax
                : 0f;

        boolean isOverallPassed = marks.stream().allMatch(StudentSubjectMark::isPassed);

        return StudentResultResponseDto.builder()
                .letterheadUrl(letterhead != null ? letterhead.getUrl() : null)
                .studentName(student.getFirstName() + " " + student.getLastName())
                .registrationId(student.getRegId())
                .className(student.getAssignedClass() != null
                        ? student.getAssignedClass().getClassName() : "N/A")
                .rollNo(student.getRollNumber())
                .examTerm(examTerm)
                .academicYear(academicYear)
                .subjectMarks(subjectMarkDtos)
                .grandTotalObtained(grandTotalObtained)
                .grandTotalMax(grandTotalMax)
                .percentage(percentage)
                .isOverallPassed(isOverallPassed)
                .build();
    }
}
