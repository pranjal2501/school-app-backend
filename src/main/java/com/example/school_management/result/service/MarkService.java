package com.example.school_management.result.service;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.academics.repository.ClassSubjectTeacherRepository;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.MarkFillingStatus;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.exam.entity.MarkingScheme;
import com.example.school_management.exam.entity.MarkingSchemeDetail;
import com.example.school_management.exam.repository.EvaluationComponentRepository;
import com.example.school_management.exam.repository.ExamTypeRepository;
import com.example.school_management.result.dto.ComponentMarkInputDto;
import com.example.school_management.result.dto.MarkResponseDto;
import com.example.school_management.result.dto.MarkUploadRequestDto;
import com.example.school_management.result.dto.StudentMarkEntryDto;
import com.example.school_management.result.entity.StudentSubjectMark;
import com.example.school_management.result.repository.StudentSubjectMarkRepository;
import com.example.school_management.student.entity.Student;
import com.example.school_management.student.repository.StudentRepository;
import com.example.school_management.subject.entity.Subject;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarkService {

    private final StudentSubjectMarkRepository markRepository;
    private final ClassSubjectTeacherRepository pivotRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ExamTypeRepository examTypeRepository;
    private final EvaluationComponentRepository evaluationComponentRepository;
    private final RoleVerifier roleVerifier;

    private Teacher getCurrentTeacher() {
        if (!roleVerifier.hasRole(Role.TEACHER)) {
            throw new AccessDeniedException("Only logged-in teachers can perform this action.");
        }
        String identifier = roleVerifier.getCurrentUsername();
        return teacherRepository.findByEmail(identifier)
                .or(() -> teacherRepository.findByMobile(identifier))
                .orElseThrow(() -> new AccessDeniedException("Teacher profile could not be verified."));
    }

    private ClassSubjectTeacher verifyAndGetAllocation(Long allocationId, Teacher currentTeacher) {
        ClassSubjectTeacher allocation = pivotRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Class/Subject Allocation", "id", allocationId));

        if (!allocation.getTeacher().getId().equals(currentTeacher.getId())) {
            throw new AccessDeniedException("UNAUTHORIZED: You are not the assigned subject teacher for this class and subject.");
        }
        return allocation;
    }

    @Transactional
    public List<MarkResponseDto> uploadOrUpdateMarks(MarkUploadRequestDto request) {
        Teacher currentTeacher = getCurrentTeacher();
        ClassSubjectTeacher allocation = verifyAndGetAllocation(request.getAllocationId(), currentTeacher);
        Subject subject = allocation.getSubject();

        if (subject.isAggregateOnly()) {
            throw new IllegalArgumentException("Cannot upload marks directly to an aggregate parent subject.");
        }

        MarkingScheme currentScheme = subject.getMarkingSchemes().stream()
                .filter(scheme -> scheme.getExamType().getId().equals(request.getExamTypeId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Marking Scheme", "ExamType ID", request.getExamTypeId()));

        Map<Long, MarkingSchemeDetail> componentDetailMap = currentScheme.getDetails().stream()
                .collect(Collectors.toMap(d -> d.getEvaluationComponent().getId(), d -> d));

        List<StudentSubjectMark> savedMarks = new ArrayList<>();

        for (StudentMarkEntryDto entry : request.getMarks()) {
            Student student = studentRepository.findById(entry.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student", "id", entry.getStudentId()));

            if (!student.getAssignedClass().getId().equals(allocation.getSchoolClass().getId())) {
                throw new IllegalArgumentException("Student ID " + student.getId() + " does not belong to class " + allocation.getSchoolClass().getClassName());
            }

            validateComponentMarksAgainstScheme(entry, componentDetailMap, subject.getSubjectName());

            for (ComponentMarkInputDto compInput : entry.getComponentMarks()) {

                StudentSubjectMark mark = markRepository.findByStudentIdAndAllocationIdAndExamTypeIdAndEvaluationComponentId(
                        student.getId(),
                        allocation.getId(),
                        request.getExamTypeId(),
                        compInput.getEvaluationComponentId()
                ).orElse(null);

                if (mark != null) {
                    mark.setObtainedMarks(compInput.getObtainedMarks());
                } else {
                    mark = StudentSubjectMark.builder()
                            .student(student)
                            .allocation(allocation)
                            .examType(examTypeRepository.getReferenceById(request.getExamTypeId()))
                            .evaluationComponent(evaluationComponentRepository.getReferenceById(compInput.getEvaluationComponentId()))
                            .obtainedMarks(compInput.getObtainedMarks())
                            .build();
                }
                savedMarks.add(markRepository.save(mark));
            }
        }

        updateMarkFillingStatus(allocation, request.getExamTypeId());

        return mapToAggregatedResponse(savedMarks, currentScheme);
    }

    @Transactional(readOnly = true)
    public List<MarkResponseDto> getMarksForAllocation(Long allocationId, Long examTypeId) {
        Teacher currentTeacher = getCurrentTeacher();
        ClassSubjectTeacher allocation = verifyAndGetAllocation(allocationId, currentTeacher);

        MarkingScheme currentScheme = allocation.getSubject().getMarkingSchemes().stream()
                .filter(scheme -> scheme.getExamType().getId().equals(examTypeId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Marking Scheme", "ExamType ID", examTypeId));

        List<StudentSubjectMark> marks = markRepository.findAllByAllocationIdAndExamTypeId(allocationId, examTypeId);

        return mapToAggregatedResponse(marks, currentScheme);
    }

    private void validateComponentMarksAgainstScheme(StudentMarkEntryDto entry, Map<Long, MarkingSchemeDetail> validComponents, String subjectName) {
        for (ComponentMarkInputDto compInput : entry.getComponentMarks()) {
            MarkingSchemeDetail detail = validComponents.get(compInput.getEvaluationComponentId());

            if (detail == null) {
                throw new IllegalArgumentException("Evaluation Component ID " + compInput.getEvaluationComponentId() + " is not mapped to this exam type for subject " + subjectName);
            }

            if (compInput.getObtainedMarks() > detail.getMaxMarks()) {
                throw new IllegalArgumentException(String.format(
                        "Marks obtained (%d) exceed maximum allowed (%d) for component %s in subject %s.",
                        compInput.getObtainedMarks(), detail.getMaxMarks(), detail.getEvaluationComponent().getName(), subjectName));
            }
        }
    }

    private void updateMarkFillingStatus(ClassSubjectTeacher allocation, Long examTypeId) {
        int totalStudents = studentRepository.countByAssignedClassId(allocation.getSchoolClass().getId());

        int uniqueStudentsWithMarks = markRepository.countDistinctStudentsByAllocationIdAndExamTypeId(
                allocation.getId(), examTypeId);

        if (uniqueStudentsWithMarks == 0) {
            allocation.setMarkFillingStatus(MarkFillingStatus.NOT_FILLED);
        } else if (uniqueStudentsWithMarks < totalStudents) {
            allocation.setMarkFillingStatus(MarkFillingStatus.PARTIAL);
        } else {
            allocation.setMarkFillingStatus(MarkFillingStatus.COMPLETE);
        }

        pivotRepository.save(allocation);
    }

    private List<MarkResponseDto> mapToAggregatedResponse(List<StudentSubjectMark> marks, MarkingScheme scheme) {
        Map<Long, List<StudentSubjectMark>> marksByStudent = marks.stream()
                .collect(Collectors.groupingBy(m -> m.getStudent().getId()));

        List<MarkResponseDto> responseList = new ArrayList<>();

        for (Map.Entry<Long, List<StudentSubjectMark>> studentEntry : marksByStudent.entrySet()) {
            Student student = studentEntry.getValue().get(0).getStudent();
            int totalObtained = 0;
            int totalMax = 0;
            boolean isPassed = true;

            List<com.example.school_management.result.dto.ComponentMarkResponseDto> compResponses = new ArrayList<>();

            for (StudentSubjectMark sm : studentEntry.getValue()) {
                MarkingSchemeDetail detail = scheme.getDetails().stream()
                        .filter(d -> d.getEvaluationComponent().getId().equals(sm.getEvaluationComponent().getId()))
                        .findFirst().orElse(null);

                if (detail == null) continue;

                boolean compPassed = sm.getObtainedMarks() >= detail.getPassingMarks();
                if (!compPassed) isPassed = false;

                totalObtained += sm.getObtainedMarks();
                totalMax += detail.getMaxMarks();

                compResponses.add(com.example.school_management.result.dto.ComponentMarkResponseDto.builder()
                        .markRecordId(sm.getId())
                        .evaluationComponentId(sm.getEvaluationComponent().getId())
                        .componentName(detail.getEvaluationComponent().getName())
                        .maxMarks(detail.getMaxMarks())
                        .passingMarks(detail.getPassingMarks())
                        .obtainedMarks(sm.getObtainedMarks())
                        .isComponentPassed(compPassed)
                        .build());
            }

            responseList.add(MarkResponseDto.builder()
                    .studentId(student.getId())
                    .studentName(student.getFirstName() + " " + student.getLastName())
                    .rollNumber(student.getRollNumber())
                    .examTypeId(scheme.getExamType().getId())
                    .examTypeName(scheme.getExamType().getName())
                    .componentMarks(compResponses)
                    .totalObtained(totalObtained)
                    .totalMaxMarks(totalMax)
                    .isPassed(isPassed)
                    .build());
        }

        return responseList;
    }
}