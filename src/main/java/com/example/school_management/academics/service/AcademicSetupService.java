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
import com.example.school_management.admin.entity.Admin;
import com.example.school_management.admin.repository.AdminRepository;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.schoolClass.dto.ClassResponseDto;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.schoolClass.mapper.ClassMapper;
import com.example.school_management.schoolClass.repo.SchoolClassRepository;
import com.example.school_management.subject.entity.Subject;
import com.example.school_management.subject.repository.SubjectRepository;
import com.example.school_management.teacher.dto.TeacherResponseDto;
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

    private void verifyAndGetAdminRecord() {
        if (!roleVerifier.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Access Denied: Only Admins can perform this action.");
        }
        adminRepository.findByUsername(roleVerifier.getCurrentUsername())
                .orElseThrow(() -> new AccessDeniedException("Admin record not found."));
    }

    @Transactional(readOnly = true)
    public List<SubjectWithTeachersResponseDto> getSubjectsByStandard(String standard, String username) {
//        verifyAndGetAdminRecord(); // Vault Check

        // 1. Fetch all subjects for this standard
        List<Subject> subjects = subjectRepository.findAllByStandard(standard);

        // 2. Fetch all teacher allocations for this entire standard in ONE query (Performance Optimization)
        List<ClassSubjectTeacher> standardAllocations = pivotRepository.findAllBySubject_Standard(standard);

        // 3. Map everything securely together
        return subjects.stream().map(subject -> {

            // Find all distinct teachers assigned to this specific subject across all divisions
            List<TeacherResponseDto> assignedTeachers = standardAllocations.stream()
                    .filter(mapping -> mapping.getSubject().getId().equals(subject.getId()))
                    .map(ClassSubjectTeacher::getTeacher)
                    .distinct() // Removes duplicates (e.g., if Mr. Smith teaches both 6-A and 6-B)
                    .map(teacherMapper::toResponseDto)
                    .toList();

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
                    .assignedTeachers(assignedTeachers)
                    .build();
        }).toList();
    }

    @Transactional
    public List<ClassResponseDto> setupStandardAndClasses(ClassBulkSetupRequestDto request) {
        verifyAndGetAdminRecord();

        String standard = request.getStandard().trim();
        List<SchoolClass> activeClasses = new ArrayList<>();

        for (String divInput : request.getDivisions()) {
            String division = divInput.trim();

            // 1. Upsert the Academic Structure (Lookup Table)
            AcademicStructure structure = academicStructureRepository
                    .findByStandardAndDivision(standard, division)
                    .orElseGet(() -> {
                        AcademicStructure newStructure = AcademicStructure.builder()
                                .standard(standard)
                                .division(division)
                                .build();
                        return academicStructureRepository.save(newStructure);
                    });

            // 2. Upsert the actual physical SchoolClass
            SchoolClass schoolClass = classRepository.findByAcademicStructureId(structure.getId())
                    .orElseGet(() -> {
                        SchoolClass newClass = new SchoolClass();
                        newClass.setAcademicStructure(structure);
                        newClass.setClassName(standard + "-" + division);

                        // Default zero-states for a brand new empty classroom
                        newClass.setBoys(0);
                        newClass.setGirls(0);
                        newClass.setCurrentBoys(0);
                        newClass.setCurrentGirls(0);
                        newClass.setTotalStudents(0);

                        return classRepository.save(newClass);
                    });

            activeClasses.add(schoolClass);
        }

        // Return the created/existing classes mapped to your standard DTO
        return activeClasses.stream()
                // Assuming you have access to your classMapper in this service
                .map(classMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClassSubjectTeacher> getAllocationsByClass(Long classId) {
        // Strict security check mimicking your existing methods
        verifyAndGetAdminRecord();

        if (!classRepository.existsById(classId)) {
            throw new ResourceNotFoundException("Class", "id", classId);
        }

        return pivotRepository.findAllBySchoolClassId(classId);
    }

    /**
     * Fulfills Point #3: Creates subjects and applies them to ALL classes in the Standard.
     */
    @Transactional
    public void applyTemplateToStandard(StandardTemplateRequestDto request, String username) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        // 1. Fetch all classes for this standard (e.g., 10-A, 10-B)
        List<SchoolClass> classesInStandard = classRepository.findAllByAcademicStructure_Standard(request.getStandard());
        if (classesInStandard.isEmpty()) {
            throw new RuntimeException("No classes found for standard: " + request.getStandard());
        }

        // 2. Iterate through the UI mini-forms (Subjects)
        for (StandardTemplateRequestDto.SubjectTemplateDto dto : request.getSubjects()) {

            // A. Save the Subject Definition
            Subject subject = Subject.builder()
                    .subjectName(dto.getSubjectName())
                    .standard(request.getStandard())
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
            subject = subjectRepository.save(subject);

            // B. Fetch the selected default Teacher (e.g., Mr. Smith)
            Teacher defaultTeacher = teacherRepository.findById(dto.getDefaultTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            // C. Bulk Allocate: Loop through 10-A, 10-B and map the pivot table
            for (SchoolClass schoolClass : classesInStandard) {

                // Upsert logic: Check if this class already has this subject mapped
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

    /**
     * Fulfills Point #4: Allows Admin to change the teacher for a specific class (e.g., 10-B English -> Mr. Jonathan)
     */
    @Transactional
    public void overrideSubjectTeacher(Long classId, ClassOverrideRequestDto request, String username) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        ClassSubjectTeacher mapping = pivotRepository
                .findBySchoolClassIdAndSubjectId(classId, request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("This subject is not assigned to this class yet."));

        Teacher newTeacher = teacherRepository.findById(request.getNewTeacherId())
                .orElseThrow(() -> new RuntimeException("New Teacher not found"));

        // Override Mr. Smith with Mr. Jonathan just for this specific class
        mapping.setTeacher(newTeacher);
        pivotRepository.save(mapping);
    }
    /**
     * WORKFLOW: Deallocate a subject from a class (e.g., Remove Sanskrit from 10-L)
     * This safely deletes the pivot entry, freeing the teacher and updating the class timetable.
     * The Subject itself remains in the DB in case other classes still use it.
     */
    @Transactional
    public void removeSubjectFromClass(Long classId, Long subjectId,String username) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        ClassSubjectTeacher mapping = pivotRepository
                .findBySchoolClassIdAndSubjectId(classId, subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation", "classId/subjectId", classId + "/" + subjectId));

        pivotRepository.delete(mapping);
    }

    /**
     * WORKFLOW: Create a BRAND NEW subject and assign it strictly to one class (e.g., Create Hindi for 10-L)
     */
    @Transactional
    public void createAndAllocateSubjectToClass(Long classId, StandardTemplateRequestDto.SubjectTemplateDto request, String username) {

        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        SchoolClass schoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", classId));

        Teacher teacher = teacherRepository.findById(request.getDefaultTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", request.getDefaultTeacherId()));

        // 1. Save the new Subject definition
        Subject subject = Subject.builder()
                .subjectName(request.getSubjectName())
                .standard(schoolClass.getAcademicStructure().getStandard()) // Inherit standard from the class
                .subjectType(request.getSubjectType())
                .theoryMarks(request.getTheoryMarks())
                .practicalMarks(request.getPracticalMarks())
                .totalMarks(request.getTotalMarks())
                .passingMarks(request.getPassingMarks())
                .weeklyHours(request.getWeeklyHours())
                .build();

        subject = subjectRepository.save(subject);

        // 2. CRITICAL UX STEP: Because this subject is brand new, the teacher won't be qualified for it yet!
        // To prevent forcing the Admin to do 2 separate steps, we auto-qualify the teacher here.
        if (!teacher.getSubjects().contains(subject)) {
            teacher.getSubjects().add(subject);
            teacherRepository.save(teacher);
        }

        // 3. Create the Pivot Mapping
        ClassSubjectTeacher mapping = new ClassSubjectTeacher();
        mapping.setSchoolClass(schoolClass);
        mapping.setSubject(subject);
        mapping.setTeacher(teacher);

        pivotRepository.save(mapping);
//        log.info("Admin '{}' created new subject '{}' and allocated it to Class ID {}", adminIdentifier, subject.getSubjectName(), classId);
    }

    /**
     * WORKFLOW: Assign an EXISTING subject pool to a class (e.g., 10-A already created Hindi, now add it to 10-L)
     */
    @Transactional
    public void allocateExistingSubjectToClass(Long classId, ClassSubjectAllocationRequestDto request, String username) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        SchoolClass schoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", classId));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));

        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", request.getTeacherId()));

        // Ensure teacher is actually qualified for this existing subject
        boolean isQualified = teacher.getSubjects().stream()
                .anyMatch(s -> s.getId().equals(subject.getId()));

        if (!isQualified) {
            throw new IllegalArgumentException("Teacher is not qualified to teach " + subject.getSubjectName());
        }

        // Check if allocation already exists to prevent duplicate entries
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
        verifyAndGetAdminRecord(); // Vault Check
        return academicStructureRepository.findDistinctStandards();
    }

    @Transactional(readOnly = true)
    public List<AcademicStructureResponseDto> getDivisionsByStandard(String standard) {
        verifyAndGetAdminRecord(); // Vault Check

        List<AcademicStructure> structures = academicStructureRepository.findAllByStandard(standard);

        // Map the entities to DTOs
        return structures.stream()
                .map(structure -> AcademicStructureResponseDto.builder()
                        .id(structure.getId())
                        .standard(structure.getStandard())
                        .division(structure.getDivision())
                        .build())
                .toList();
    }
}