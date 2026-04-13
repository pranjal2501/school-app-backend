package com.example.school_management.academics;

import com.example.school_management.academics.dto.ClassOverrideRequestDto;
import com.example.school_management.academics.dto.ClassSubjectAllocationRequestDto;
import com.example.school_management.academics.dto.StandardTemplateRequestDto;
import com.example.school_management.admin.Admin;
import com.example.school_management.admin.AdminRepository;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.schoolClass.repo.SchoolClassRepository;
import com.example.school_management.subject.Subject;
import com.example.school_management.subject.SubjectRepository;
import com.example.school_management.teacher.Teacher;
import com.example.school_management.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicSetupService {

    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository classRepository;
    private final TeacherRepository teacherRepository;
    private final ClassSubjectTeacherRepository pivotRepository;
    private final AdminRepository adminRepository;


    /**
     * Fulfills Point #3: Creates subjects and applies them to ALL classes in the Standard.
     */
    @Transactional
    public void applyTemplateToStandard(StandardTemplateRequestDto request, String username) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        // 1. Fetch all classes for this standard (e.g., 10-A, 10-B)
        List<SchoolClass> classesInStandard = classRepository.findAllByStandard(request.getStandard());

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
                .standard(schoolClass.getStandard()) // Inherit standard from the class
                .subjectType(request.getSubjectType())
                .theoryMarks(request.getTheoryMarks())
                .practicalMarks(request.getPracticalMarks())
                .totalMarks(request.getTotalMarks())
                .passingMarks(request.getPassingMarks())
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
}