package com.example.school_management.mapper;

import com.example.school_management.dto.SubjectRequestDto;
import com.example.school_management.dto.SubjectResponseDto;
import com.example.school_management.entity.Subject;
import com.example.school_management.entity.Teacher;
import com.example.school_management.enums.SubjectType;
import com.example.school_management.repo.TeacherRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SubjectMapper {

    private final TeacherRepo teacherRepository;

    public Subject toEntity(SubjectRequestDto dto) {
        Subject subject = new Subject();
        applyDtoToEntity(dto, subject);
        return subject;
    }

    public void updateEntity(SubjectRequestDto dto, Subject subject) {
        applyDtoToEntity(dto, subject);
    }

    public SubjectResponseDto toResponseDto(Subject subject) {
        SubjectResponseDto response = new SubjectResponseDto();
        response.setId(subject.getId());
        response.setSubjectName(subject.getSubjectName());
        response.setSubjectCode(subject.getSubjectCode());
        response.setStandard(subject.getStandard());
        response.setWeeklyHours(subject.getWeeklyHours());
        response.setSubjectType(subject.getSubjectType());
        response.setTheoryMarks(subject.getTheoryMarks());
        response.setInternalMarks(subject.getInternalMarks());
        response.setPracticalMarks(subject.getPracticalMarks());
        response.setProjectMarks(subject.getProjectMarks());
        response.setOralMarks(subject.getOralMarks());
        response.setTotalMarks(subject.getTotalMarks());
        response.setPassingMarks(subject.getPassingMarks());

        // Map list of teachers to list of IDs and concatenated names
        List<Teacher> teachers = subject.getSubjectTeachers();
        if (teachers != null && !teachers.isEmpty()) {
            response.setSubjectTeacherIds(
                    teachers.stream()
                            .map(Teacher::getId)
                            .collect(Collectors.toList())
            );
            response.setSubjectTeacherNames(
                    teachers.stream()
                            .map(t -> t.getFirstName() + " " + t.getLastName())
                            .collect(Collectors.toList())
            );
        }

        return response;
    }

    public List<SubjectResponseDto> toResponseDtoList(List<Subject> subjects) {
        return subjects.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    private void applyDtoToEntity(SubjectRequestDto dto, Subject subject) {
        subject.setSubjectName(dto.getSubjectName());
        subject.setSubjectCode(dto.getSubjectCode());
        subject.setWeeklyHours(dto.getWeeklyHours());
        subject.setSubjectType(dto.getSubjectType());
        subject.setTheoryMarks(dto.getTheoryMarks());
        subject.setInternalMarks(dto.getInternalMarks());
        subject.setPracticalMarks(dto.getPracticalMarks());
        subject.setProjectMarks(dto.getProjectMarks());
        subject.setOralMarks(dto.getOralMarks());
        subject.setTotalMarks(dto.getTotalMarks());
        subject.setPassingMarks(dto.getPassingMarks());

        subject.setStandard(
                dto.getSubjectType() == SubjectType.COMPULSORY ? null : dto.getStandard());

        // Handle multiple teacher IDs from DTO
        if (dto.getSubjectTeacherIds() != null && !dto.getSubjectTeacherIds().isEmpty()) {
            List<Teacher> teachers = teacherRepository.findAllById(dto.getSubjectTeacherIds());
            if (teachers.size() != dto.getSubjectTeacherIds().size()) {
                throw new RuntimeException("One or more teachers not found");
            }
            subject.setSubjectTeachers(teachers);
        } else {
            subject.setSubjectTeachers(new ArrayList<>());
        }
    }
}