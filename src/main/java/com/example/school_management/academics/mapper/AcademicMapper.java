package com.example.school_management.academics.mapper;

import com.example.school_management.academics.dto.request.StandardTemplateRequestDto;
import com.example.school_management.academics.dto.response.AcademicStructureResponseDto;
import com.example.school_management.academics.dto.response.SubjectWithTeachersResponseDto;
import com.example.school_management.academics.entity.AcademicStructure;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.subject.entity.Subject;
import com.example.school_management.teacher.dto.TeacherResponseDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AcademicMapper {

    public SubjectWithTeachersResponseDto toSubjectWithTeachersResponseDto(Subject subject, List<TeacherResponseDto> assignedTeachers) {
        if (subject == null) {
            return null;
        }

        return SubjectWithTeachersResponseDto.builder()
                .subjectId(subject.getId())
                .subjectName(subject.getSubjectName())
                .subjectCode(subject.getSubjectCode())
                .subjectType(subject.getSubjectType())
                .theoryMarks(subject.getTheoryMarks())
                .practicalMarks(subject.getPracticalMarks())
                .totalMarks(subject.getTotalMarks())
                .passingMarks(subject.getPassingMarks())
                .weeklyHours(subject.getWeeklyHours())
                .assignedTeachers(assignedTeachers != null ? assignedTeachers : new ArrayList<>())
                .build();
    }

    public AcademicStructure toAcademicStructureEntity(String standard, String division) {
        if (standard == null || division == null) {
            return null;
        }

        return AcademicStructure.builder()
                .standard(standard)
                .division(division)
                .build();
    }

    public SchoolClass toNewSchoolClassEntity(AcademicStructure structure, String standard, String division) {
        if (structure == null) {
            return null;
        }

        String safeStandard = standard != null ? standard : "";
        String safeDivision = division != null ? division : "";

        SchoolClass newClass = new SchoolClass();
        newClass.setAcademicStructure(structure);
        newClass.setClassName(safeStandard + "-" + safeDivision);

        // Default zero-states for a brand new empty classroom
        newClass.setBoys(30);
        newClass.setGirls(30);
        newClass.setCurrentBoys(0);
        newClass.setCurrentGirls(0);
        newClass.setTotalStudents(60);

        return newClass;
    }

    public Subject toSubjectEntity(StandardTemplateRequestDto.SubjectTemplateDto dto, String standard) {
        if (dto == null) {
            return null;
        }

        return Subject.builder()
                .subjectName(dto.getSubjectName())
                .standard(standard)
                .subjectType(dto.getSubjectType())
                .theoryMarks(dto.getTheoryMarks())
                .practicalMarks(dto.getPracticalMarks())
                .totalMarks(dto.getTotalMarks())
                .passingMarks(dto.getPassingMarks())
                .internalMarks(dto.getInternalMarks())
                .projectMarks(dto.getProjectMarks())
                .oralMarks(dto.getOralMarks())
                .weeklyHours(dto.getWeeklyHours())
                .build();
    }

    public AcademicStructureResponseDto toAcademicStructureResponseDto(AcademicStructure structure) {
        if (structure == null) {
            return null;
        }

        return AcademicStructureResponseDto.builder()
                .id(structure.getId())
                .standard(structure.getStandard())
                .division(structure.getDivision())
                .build();
    }
}