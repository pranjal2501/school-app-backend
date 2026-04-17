package com.example.school_management.result.service;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.academics.repository.ClassSubjectTeacherRepository;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.ExamTerm;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.result.dto.MarkResponseDto;
import com.example.school_management.result.dto.MarkUploadRequestDto;
import com.example.school_management.result.dto.StudentMarkEntryDto;
import com.example.school_management.result.entity.StudentSubjectMark;
import com.example.school_management.result.mapper.MarkMapper;
import com.example.school_management.result.repository.StudentSubjectMarkRepository;
import com.example.school_management.student.entity.Student;
import com.example.school_management.student.repository.StudentRepository;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarkService {

    private final StudentSubjectMarkRepository markRepository;
    private final ClassSubjectTeacherRepository pivotRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final MarkMapper markMapper;
    private final RoleVerifier roleVerifier;

    private Teacher getCurrentTeacher() {
        if (!roleVerifier.hasRole(Role.TEACHER)) {
            throw new AccessDeniedException("Only logged-in teachers can perform this action."); // 403 FORBIDDEN
        }
        String identifier = roleVerifier.getCurrentUsername();
        return teacherRepository.findByEmail(identifier)
                .or(() -> teacherRepository.findByMobile(identifier))
                .orElseThrow(() -> new AccessDeniedException("Teacher profile could not be verified.")); // 403 FORBIDDEN
    }

    private ClassSubjectTeacher verifyAndGetAllocation(Long allocationId, Teacher currentTeacher) {
        ClassSubjectTeacher allocation = pivotRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Class/Subject Allocation", "id", allocationId)); // 404 NOT FOUND

        // THE VAULT CHECK: Strictly prevent Teacher A from modifying Teacher B's subject marks.
        if (!allocation.getTeacher().getId().equals(currentTeacher.getId())) {
            throw new AccessDeniedException(
                    "UNAUTHORIZED: You are not the assigned subject teacher for this class and subject."); // 403 FORBIDDEN
        }
        return allocation;
    }

    @Transactional
    public List<MarkResponseDto> uploadOrUpdateMarks(MarkUploadRequestDto request) {
        Teacher currentTeacher = getCurrentTeacher();
        ClassSubjectTeacher allocation = verifyAndGetAllocation(request.getAllocationId(), currentTeacher);

        List<StudentSubjectMark> savedMarks = new ArrayList<>();

        for (StudentMarkEntryDto entry : request.getMarks()) {
            Student student = studentRepository.findById(entry.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student", "id", entry.getStudentId())); // 404 NOT FOUND

            // Check if student actually belongs to this class!
            if (!student.getAssignedClass().getId().equals(allocation.getSchoolClass().getId())) {
                throw new IllegalArgumentException(
                        "Student ID " + student.getId() + " does not belong to class " + allocation.getSchoolClass().getClassName()); // 400 BAD REQUEST
            }

            // Upsert Logic: Does the mark already exist?
            StudentSubjectMark mark = markRepository.findByStudentIdAndAllocationIdAndExamTerm(
                    student.getId(), allocation.getId(), request.getExamTerm()
            ).orElse(null);

            if (mark != null) {
                // Update existing record
                markMapper.updateEntity(entry, mark);
            } else {
                // Create new record
                mark = markMapper.toEntity(entry, student, allocation, request.getExamTerm());
            }

            savedMarks.add(markRepository.save(mark));
        }

        return savedMarks.stream().map(markMapper::toResponseDto).toList();
    }

    @Transactional(readOnly = true)
    public List<MarkResponseDto> getMarksForAllocation(Long allocationId, ExamTerm examTerm) {
        Teacher currentTeacher = getCurrentTeacher();
        verifyAndGetAllocation(allocationId, currentTeacher); // Ensures only the rightful teacher can view this specific grid

        return markRepository.findAllByAllocationIdAndExamTerm(allocationId, examTerm)
                .stream()
                .map(markMapper::toResponseDto)
                .toList();
    }
}