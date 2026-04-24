package com.example.school_management.academics.service;

import com.example.school_management.academics.dto.request.ClassBulkSetupRequestDto;
import com.example.school_management.academics.dto.request.ClassOverrideRequestDto;
import com.example.school_management.academics.dto.request.ClassSubjectAllocationRequestDto;
import com.example.school_management.academics.dto.request.StandardTemplateRequestDto;
import com.example.school_management.academics.dto.response.AcademicStructureResponseDto;
import com.example.school_management.academics.dto.response.SubjectWithTeachersResponseDto;
import com.example.school_management.academics.entity.AcademicStructure;
import com.example.school_management.academics.repository.AcademicStructureRepository;
import com.example.school_management.academics.repository.ClassSubjectTeacherRepository;
import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.admin.repository.AdminRepository;
import com.example.school_management.academics.mapper.AcademicMapper;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.schoolClass.dto.ClassResponseDto;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.schoolClass.mapper.ClassMapper;
import com.example.school_management.schoolClass.repo.SchoolClassRepository;
import com.example.school_management.subject.entity.Subject;
import com.example.school_management.subject.repository.SubjectRepository;
import com.example.school_management.teacher.dto.response.TeacherResponseDto;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.teacher.mapper.TeacherMapper;
import com.example.school_management.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicSetupService {

    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository classRepository;
    private final TeacherRepository teacherRepository;
    private final ClassSubjectTeacherRepository pivotRepository;
    private final AdminRepository adminRepository;
    private final TeacherMapper teacherMapper;
    private final AcademicStructureRepository academicStructureRepository;
    private final ClassMapper classMapper;
    private final RoleVerifier roleVerifier;
    private final AcademicMapper academicMapper;

    private void verifyAndGetAdminRecord() {
        if (!roleVerifier.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Access Denied: Only Admins can perform this action.");
        }
        adminRepository.findByUsername(roleVerifier.getCurrentUsername())
                .orElseThrow(() -> new AccessDeniedException("Admin record not found."));
    }

    @Transactional(readOnly = true)
    public List<SubjectWithTeachersResponseDto> getSubjectsByStandard(String standard) {
        verifyAndGetAdminRecord();

        List<Subject> subjects = subjectRepository.findAllByStandard(standard);
        List<ClassSubjectTeacher> standardAllocations = pivotRepository.findAllBySubject_Standard(standard);

        return subjects.stream().map(subject -> {
            List<TeacherResponseDto> assignedTeachers = standardAllocations.stream()
                    .filter(mapping -> mapping.getSubject().getId().equals(subject.getId()))
                    .map(ClassSubjectTeacher::getTeacher)
                    .distinct()
                    .map(teacherMapper::toResponseDto)
                    .toList();

            return academicMapper.toSubjectWithTeachersResponseDto(subject, assignedTeachers);
        }).toList();
    }

    @Transactional
    public List<ClassResponseDto> setupStandardAndClasses(ClassBulkSetupRequestDto request) {
        verifyAndGetAdminRecord();

        String standard = request.getStandard().trim();
        List<SchoolClass> activeClasses = new ArrayList<>();

        for (String divInput : request.getDivisions()) {
            String division = divInput.trim();

            AcademicStructure structure = academicStructureRepository
                    .findByStandardAndDivision(standard, division)
                    .orElseGet(() -> {
                        AcademicStructure newStructure = academicMapper.toAcademicStructureEntity(standard, division);
                        return academicStructureRepository.save(newStructure);
                    });

            SchoolClass schoolClass = classRepository.findByAcademicStructureId(structure.getId())
                    .orElseGet(() -> {
                        SchoolClass newClass = academicMapper.toNewSchoolClassEntity(structure, standard, division);
                        return classRepository.save(newClass);
                    });

            activeClasses.add(schoolClass);
        }

        return activeClasses.stream()
                .map(classMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SubjectWithTeachersResponseDto> getAllocationsByClass(Long classId) {
        verifyAndGetAdminRecord();

        if (!classRepository.existsById(classId)) {
            throw new ResourceNotFoundException("Class", "id", classId);
        }

        // 1. Fetch the raw entities
        List<ClassSubjectTeacher> allocations = pivotRepository.findAllBySchoolClassId(classId);

        // 2. Safely map the Entities to DTOs so Jackson doesn't trip over Lazy Proxies!
        return allocations.stream().map(mapping ->
                academicMapper.toSubjectWithTeachersResponseDto(
                        mapping.getSubject(),
                        // Wrap the single assigned teacher in a List to match your DTO structure
                        List.of(teacherMapper.toResponseDto(mapping.getTeacher()))
                )
        ).toList();
    }

    @Transactional
    public void applyTemplateToStandard(StandardTemplateRequestDto request) {
        verifyAndGetAdminRecord();

        List<SchoolClass> classesInStandard = classRepository.findAllByAcademicStructure_Standard(request.getStandard());
        if (classesInStandard.isEmpty()) {
            throw new RuntimeException("No classes found for standard: " + request.getStandard());
        }

        for (StandardTemplateRequestDto.SubjectTemplateDto dto : request.getSubjects()) {

            Subject subject = academicMapper.toSubjectEntity(dto, request.getStandard());
            subject = subjectRepository.save(subject);

            Teacher defaultTeacher = teacherRepository.findById(dto.getDefaultTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            for (SchoolClass schoolClass : classesInStandard) {
                ClassSubjectTeacher mapping = pivotRepository
                        .findBySchoolClassIdAndSubjectId(schoolClass.getId(), subject.getId())
                        .orElse(new ClassSubjectTeacher());
                mapping.setSchoolClass(schoolClass);
                mapping.setSubject(subject);
                mapping.setTeacher(defaultTeacher);
                pivotRepository.save(mapping);
            }
        }
    }

    @Transactional
    public void overrideSubjectTeacher(Long classId, ClassOverrideRequestDto request) {
        verifyAndGetAdminRecord();

        ClassSubjectTeacher mapping = pivotRepository
                .findBySchoolClassIdAndSubjectId(classId, request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("This subject is not assigned to this class yet."));

        Teacher newTeacher = teacherRepository.findById(request.getNewTeacherId())
                .orElseThrow(() -> new RuntimeException("New Teacher not found"));

        mapping.setTeacher(newTeacher);
        pivotRepository.save(mapping);
    }

    @Transactional
    public void removeSubjectFromClass(Long classId, Long subjectId) {
        verifyAndGetAdminRecord();

        ClassSubjectTeacher mapping = pivotRepository
                .findBySchoolClassIdAndSubjectId(classId, subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation", "classId/subjectId", classId + "/" + subjectId));

        pivotRepository.delete(mapping);
    }

    @Transactional
    public void createAndAllocateSubjectToClass(Long classId, StandardTemplateRequestDto.SubjectTemplateDto request) {
        verifyAndGetAdminRecord();

        SchoolClass schoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", classId));

        Teacher teacher = teacherRepository.findById(request.getDefaultTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", request.getDefaultTeacherId()));

        String inheritedStandard = schoolClass.getAcademicStructure().getStandard();
        Subject subject = academicMapper.toSubjectEntity(request, inheritedStandard);

        subject = subjectRepository.save(subject);

        if (!teacher.getSubjects().contains(subject)) {
            teacher.getSubjects().add(subject);
            teacherRepository.save(teacher);
        }

        ClassSubjectTeacher mapping = new ClassSubjectTeacher();
        mapping.setSchoolClass(schoolClass);
        mapping.setSubject(subject);
        mapping.setTeacher(teacher);

        pivotRepository.save(mapping);
    }

    @Transactional
    public void allocateExistingSubjectToClass(Long classId, ClassSubjectAllocationRequestDto request) {
        verifyAndGetAdminRecord();

        SchoolClass schoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", classId));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));

        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", request.getTeacherId()));

        boolean isQualified = teacher.getSubjects().stream()
                .anyMatch(s -> s.getId().equals(subject.getId()));

        if (!isQualified) {
            throw new IllegalArgumentException("Teacher is not qualified to teach " + subject.getSubjectName());
        }

        if (pivotRepository.findBySchoolClassIdAndSubjectId(classId, subject.getId()).isPresent()) {
            throw new IllegalStateException("This subject is already allocated to this class.");
        }

        ClassSubjectTeacher mapping = new ClassSubjectTeacher();
        mapping.setSchoolClass(schoolClass);
        mapping.setSubject(subject);
        mapping.setTeacher(teacher);

        pivotRepository.save(mapping);
    }

    @Transactional(readOnly = true)
    public List<String> getAllDistinctStandards() {
        verifyAndGetAdminRecord();
        return academicStructureRepository.findDistinctStandards();
    }

    @Transactional(readOnly = true)
    public List<AcademicStructureResponseDto> getDivisionsByStandard(String standard) {
        verifyAndGetAdminRecord();

        List<AcademicStructure> structures = academicStructureRepository.findAllByStandard(standard);

        return structures.stream()
                .map(academicMapper::toAcademicStructureResponseDto)
                .toList();
    }
}