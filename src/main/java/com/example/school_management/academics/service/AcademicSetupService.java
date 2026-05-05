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
import com.example.school_management.exam.entity.EvaluationComponent;
import com.example.school_management.exam.entity.ExamType;
import com.example.school_management.exam.entity.MarkingScheme;
import com.example.school_management.exam.entity.MarkingSchemeDetail;
import com.example.school_management.exam.repository.EvaluationComponentRepository;
import com.example.school_management.exam.repository.ExamTypeRepository;
import com.example.school_management.schoolClass.dto.ClassResponseDto;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.schoolClass.mapper.ClassMapper;
import com.example.school_management.schoolClass.repo.SchoolClassRepository;
import com.example.school_management.subject.dto.BulkSubjectCreateRequestDto;
import com.example.school_management.subject.dto.newImpl.SubjectTreeResponseDto;
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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private final ExamTypeRepository examTypeRepository;
    private final EvaluationComponentRepository componentRepository;

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
    public List<SubjectTreeResponseDto> getAllocationsByClass(Long classId) {
        verifyAndGetAdminRecord();

        SchoolClass schoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", classId));

        String standard = schoolClass.getAcademicStructure().getStandard();

        List<ClassSubjectTeacher> classAllocations = pivotRepository.findAllBySchoolClassId(classId);

        Map<Long, Teacher> subjectTeacherMap = classAllocations.stream()
                .collect(Collectors.toMap(
                        mapping -> mapping.getSubject().getId(),
                        ClassSubjectTeacher::getTeacher,
                        (existing, replacement) -> existing
                ));

        List<Subject> rootSubjects = subjectRepository.findAllByStandardAndParentSubjectIsNull(standard);

        return rootSubjects.stream()
                .map(subject -> mapToTreeDto(subject, subjectTeacherMap))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private SubjectTreeResponseDto mapToTreeDto(Subject subject, Map<Long, Teacher> subjectTeacherMap) {
        SubjectTreeResponseDto dto = new SubjectTreeResponseDto();
        dto.setId(subject.getId());
        dto.setSubjectName(subject.getSubjectName());
        dto.setSubjectCode(subject.getSubjectCode());
        dto.setAggregateOnly(subject.isAggregateOnly());
        dto.setTotalMarks(subject.getTotalMarks());

        Teacher teacher = subjectTeacherMap.get(subject.getId());
        if (teacher != null) {
            dto.setAllocatedTeacher(teacherMapper.toResponseDto(teacher));
            dto.setAllocatedTeacherId(teacher.getId());
            dto.setAllocatedTeacherName(teacher.getFirstName()+" "+teacher.getLastName());
        }

        boolean hasAllocatedChildren = false;

        // 1. Map Child Subjects (For Aggregates)
        if (subject.getChildSubjects() != null && !subject.getChildSubjects().isEmpty()) {
            List<SubjectTreeResponseDto> validChildren = subject.getChildSubjects().stream()
                    .map(child -> mapToTreeDto(child, subjectTeacherMap))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!validChildren.isEmpty()) {
                dto.setChildSubjects(validChildren);
                hasAllocatedChildren = true;
            }
        }

        // If it's a Leaf Subject, and it has no teacher for this class -> PRUNE IT
        if (!subject.isAggregateOnly() && teacher == null) {
            return null;
        }

        // If it's an Aggregate Folder, and ALL its children were pruned -> PRUNE IT
        if (subject.isAggregateOnly() && !hasAllocatedChildren) {
            return null;
        }
        // ==========================================

        // 2. Map Marking Schemes (For Leaf Nodes)
        if (subject.getMarkingSchemes() != null && !subject.getMarkingSchemes().isEmpty()) {
            List<SubjectTreeResponseDto.SubjectExamResponseDto> examDtos = subject.getMarkingSchemes().stream()
                    .map(scheme -> {
                        List<SubjectTreeResponseDto.ComponentMarkResponseDto> componentDtos = scheme.getDetails().stream()
                                .map(detail -> SubjectTreeResponseDto.ComponentMarkResponseDto.builder()
                                        .evaluationComponentId(detail.getEvaluationComponent().getId())
                                        .componentName(detail.getEvaluationComponent().getName())
                                        .maxMarks(detail.getMaxMarks())
                                        .passingMarks(detail.getPassingMarks())
                                        .build())
                                .collect(Collectors.toList());

                        return SubjectTreeResponseDto.SubjectExamResponseDto.builder()
                                .markingSchemeId(scheme.getId())
                                .examTypeId(scheme.getExamType().getId())
                                .examTypeName(scheme.getExamType().getName())
                                .components(componentDtos)
                                .build();
                    })
                    .collect(Collectors.toList());

            dto.setExams(examDtos);
        }

        return dto;
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
            if (subject.isAggregateOnly()) {
                throw new IllegalArgumentException(
                        "Cannot allocate '" + subject.getSubjectName() + "' to a class because it is an Aggregate/Folder subject. " +
                                "You must allocate its child subjects (e.g., History, Geography) instead."
                );
            }
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

    // 🚀 We now use the new SubjectNodeDto so it can receive Exams
    @Transactional
    public void createAndAllocateSubjectToClass(Long classId, BulkSubjectCreateRequestDto.SubjectNodeDto request) {
        verifyAndGetAdminRecord();

        SchoolClass schoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", classId));

        Teacher teacher = teacherRepository.findById(request.getDefaultTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", request.getDefaultTeacherId()));

        if (request.isAggregateOnly()) {
            throw new IllegalArgumentException("Cannot create and allocate an aggregate subject directly to a class. Please create a leaf subject.");
        }

        String inheritedStandard = schoolClass.getAcademicStructure().getStandard();

        // 1.  Build the Subject Entity (Without legacy marks!)
        Subject subject = Subject.builder()
                .subjectName(request.getSubjectName())
                .standard(inheritedStandard)
                .weeklyHours(request.getWeeklyHours())
                .subjectType(request.getSubjectType())
                .isAggregateOnly(false)
                .childSubjects(new ArrayList<>())
                .markingSchemes(new ArrayList<>())
                .build();

        // 2.  Build and attach the Exams / Marking Schemes
        if (request.getExams() != null && !request.getExams().isEmpty()) {
            for (BulkSubjectCreateRequestDto.SubjectExamConfigDto examDto : request.getExams()) {
                ExamType examType = examTypeRepository.findById(examDto.getExamTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Exam Type not found"));

                MarkingScheme scheme = MarkingScheme.builder()
                        .examType(examType)
                        .standard(inheritedStandard)
                        .details(new ArrayList<>())
                        .build();

                for (BulkSubjectCreateRequestDto.ComponentMarkDto compDto : examDto.getComponents()) {
                    EvaluationComponent component = componentRepository.findById(compDto.getEvaluationComponentId())
                            .orElseThrow(() -> new ResourceNotFoundException("Evaluation Component not found"));

                    MarkingSchemeDetail detail = MarkingSchemeDetail.builder()
                            .evaluationComponent(component)
                            .maxMarks(compDto.getMaxMarks())
                            .passingMarks(compDto.getPassingMarks())
                            .build();

                    scheme.addDetail(detail);
                }
                subject.addMarkingScheme(scheme); // Wires up the bi-directional relationship
            }
        }

        // Save Subject (Cascades and saves all marking schemes automatically)
        subject = subjectRepository.save(subject);

        // Teacher qualification logic
        if (!teacher.getSubjects().contains(subject)) {
            teacher.getSubjects().add(subject);
            teacherRepository.save(teacher);
        }

        // Allocate to Class
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
        if (subject.isAggregateOnly()) {
            throw new IllegalArgumentException("Cannot allocate an aggregate subject to a class.");
        }
        // NEW SECURITY CHECK: Cross-Standard Allocation Prevention
        String classStandard = schoolClass.getAcademicStructure().getStandard();
        if (!classStandard.equals(subject.getStandard())) {
            throw new IllegalArgumentException("Subject '" + subject.getSubjectName() + "' belongs to Standard " + subject.getStandard() + ", but the target class is Standard " + classStandard);
        }

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