package com.example.school_management.subject.service;
import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.academics.repository.ClassSubjectTeacherRepository;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.exam.entity.EvaluationComponent;
import com.example.school_management.exam.entity.ExamType;
import com.example.school_management.exam.entity.MarkingScheme;
import com.example.school_management.exam.entity.MarkingSchemeDetail;
import com.example.school_management.exam.repository.EvaluationComponentRepository;
import com.example.school_management.exam.repository.ExamTypeRepository;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.schoolClass.repo.SchoolClassRepository;
import com.example.school_management.subject.dto.BulkSubjectCreateRequestDto;
import com.example.school_management.subject.dto.SubjectResponseDto;
import com.example.school_management.subject.dto.newImpl.SubjectRequestDto;
import com.example.school_management.subject.dto.newImpl.SubjectTreeResponseDto;
import com.example.school_management.subject.entity.Subject;
import com.example.school_management.subject.repository.SubjectRepository;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    private final ClassSubjectTeacherRepository classSubjectTeacherRepository;
    private final SubjectRepository subjectRepository;
    private final ExamTypeRepository examTypeRepository;
    private final EvaluationComponentRepository componentRepository;

    private final SchoolClassRepository classRepository;
    private final TeacherRepository teacherRepository;
    private final ClassSubjectTeacherRepository pivotRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponseDto> getSubjectsByStandard(String standard) {
        return classSubjectTeacherRepository.findDistinctSubjectsByStandard(standard)
                .stream()
                .map(subject -> SubjectResponseDto.builder()
                        .id(subject.getId())
                        .name(subject.getSubjectName()) // Adjust getter based on your Subject entity
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Subject createSubject(SubjectRequestDto dto) {
        Subject parent = null;

        if (dto.getParentSubjectId() != null) {
            parent = subjectRepository.findById(dto.getParentSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Subject not found"));

            // Safety Check: A child cannot be an aggregate, and cannot be attached to a non-aggregate
            if (!parent.isAggregateOnly()) {
                throw new IllegalArgumentException("You can only add child subjects to an Aggregate Subject.");
            }
            if (dto.isAggregateOnly()) {
                throw new IllegalArgumentException("A child subject cannot be an aggregate itself.");
            }
        }

        // Backward Compatibility: If Aggregate, force marks to 0 to bypass DB NotNull crashes
        if (dto.isAggregateOnly()) {
            dto.setTheoryMarks(0);
            dto.setInternalMarks(0);
            dto.setPracticalMarks(0);
            dto.setProjectMarks(0);
            dto.setOralMarks(0);
            dto.setTotalMarks(0);
            dto.setPassingMarks(0);
        }

        Subject subject = Subject.builder()
                .subjectName(dto.getSubjectName())
                .standard(dto.getStandard())
                .weeklyHours(dto.getWeeklyHours())
                .subjectType(dto.getSubjectType())
                .theoryMarks(dto.getTheoryMarks())
                .internalMarks(dto.getInternalMarks())
                .practicalMarks(dto.getPracticalMarks())
                .projectMarks(dto.getProjectMarks())
                .oralMarks(dto.getOralMarks())
                .totalMarks(dto.getTotalMarks())
                .passingMarks(dto.getPassingMarks())
                .isAggregateOnly(dto.isAggregateOnly())
                .parentSubject(parent)
                .build();

        return subjectRepository.save(subject);
    }

    // Gets ONLY the Leaf Nodes for Exams and Teacher Allocations
    @Transactional(readOnly = true)
    public List<Subject> getLeafSubjectsForAllocation(String standard) {
        return subjectRepository.findAllLeafSubjectsByStandard(standard);
    }

    // Builds the nested tree for the UI Admin Dashboard
    @Transactional(readOnly = true)
    @Override
    public List<SubjectTreeResponseDto> getSubjectTreeByStandard(String standard) {
        // 1. Fetch Root Subjects
        List<Subject> rootSubjects = subjectRepository.findAllByStandardAndParentSubjectIsNull(standard);

        // 2. Fetch all classes in this standard
        List<SchoolClass> classes = classRepository.findAllByAcademicStructure_Standard(standard);
        List<Long> classIds = classes.stream().map(SchoolClass::getId).collect(Collectors.toList());

        // 3. Prevent N+1: Fetch all teacher allocations for this standard IN ONE QUERY
        Map<Long, Teacher> subjectTeacherMap = new HashMap<>();
        if (!classIds.isEmpty()) {
            List<ClassSubjectTeacher> allocations = pivotRepository.findAllBySchoolClassIdIn(classIds);
            for (ClassSubjectTeacher allocation : allocations) {
                // Since this is the template view, we just grab the first teacher mapped to this subject
                // (Assuming all classes in the standard were assigned the same teacher during bulk create)
                subjectTeacherMap.putIfAbsent(allocation.getSubject().getId(), allocation.getTeacher());
            }
        }

        // 4. Map to DTO, passing the cached map down the tree
        return rootSubjects.stream()
                .map(subject -> mapToTreeDto(subject, subjectTeacherMap))
                .collect(Collectors.toList());
    }

    // Recursive Mapper
    private SubjectTreeResponseDto mapToTreeDto(Subject subject, Map<Long, Teacher> subjectTeacherMap) {
        SubjectTreeResponseDto dto = new SubjectTreeResponseDto();
        dto.setId(subject.getId());
        dto.setSubjectName(subject.getSubjectName());
        dto.setSubjectCode(subject.getSubjectCode());
        dto.setAggregateOnly(subject.isAggregateOnly());
        dto.setTotalMarks(subject.getTotalMarks());

        // NEW: Attach Teacher Allocation (If one exists in our map)
        Teacher teacher = subjectTeacherMap.get(subject.getId());
        if (teacher != null) {
            dto.setAllocatedTeacherId(teacher.getId());

            // Note: Adjust the getter methods below based on your actual Teacher entity fields (e.g., getName() or getFirstName())
            dto.setAllocatedTeacherName(teacher.getFirstName() + " " + teacher.getLastName());
        }

        // 1. Map Child Subjects (For Aggregates) - Notice we pass the map down!
        if (subject.getChildSubjects() != null && !subject.getChildSubjects().isEmpty()) {
            dto.setChildSubjects(subject.getChildSubjects().stream()
                    .map(child -> mapToTreeDto(child, subjectTeacherMap))
                    .collect(Collectors.toList()));
        }

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
    public String bulkCreateSubjects(BulkSubjectCreateRequestDto request) {
        List<Subject> topLevelSubjectsToSave = new ArrayList<>();

        Map<Subject, Long> subjectTeacherAllocations = new LinkedHashMap<>();

        for (BulkSubjectCreateRequestDto.SubjectNodeDto nodeDto : request.getSubjects()) {
            Subject rootSubject = mapToEntity(nodeDto, request.getStandard(), null, subjectTeacherAllocations);
            topLevelSubjectsToSave.add(rootSubject);
        }

        log.info("=== DIAGNOSTIC: Found {} subjects ready for teacher allocation ===", subjectTeacherAllocations.size());

        subjectRepository.saveAll(topLevelSubjectsToSave);

        if (!subjectTeacherAllocations.isEmpty()) {
            applyTemplateToClasses(request.getStandard(), subjectTeacherAllocations);
        } else {
            log.warn("=== DIAGNOSTIC: Skipping Template Allocation because the Map is EMPTY! ===");
        }

        return "Successfully configured curriculum, marking schemes, and teacher allocations for Standard " + request.getStandard();
    }

    private void applyTemplateToClasses(String standard, Map<Subject, Long> subjectTeacherAllocations) {

        List<SchoolClass> classesInStandard = classRepository.findAllByAcademicStructure_Standard(standard);

        log.info("=== DIAGNOSTIC: Found {} classes for standard {} ===", classesInStandard.size(), standard);

        if (classesInStandard.isEmpty()) {
            throw new RuntimeException("No classes found for standard: " + standard + ". Cannot allocate teachers. Please create classes (e.g., 6-A) first.");
        }

        Set<Long> uniqueTeacherIds = new HashSet<>(subjectTeacherAllocations.values());
        Map<Long, Teacher> teacherCache = teacherRepository.findAllById(uniqueTeacherIds).stream()
                .collect(Collectors.toMap(Teacher::getId, teacher -> teacher));

        List<ClassSubjectTeacher> allocationsToSave = new ArrayList<>();

        for (Map.Entry<Subject, Long> entry : subjectTeacherAllocations.entrySet()) {
            Subject savedSubject = entry.getKey();
            Long teacherId = entry.getValue();

            Teacher teacher = teacherCache.get(teacherId);
            if (teacher == null) {
                throw new ResourceNotFoundException("Teacher with ID " + teacherId + " not found.");
            }

            for (SchoolClass schoolClass : classesInStandard) {
                ClassSubjectTeacher mapping = pivotRepository
                        .findBySchoolClassIdAndSubjectId(schoolClass.getId(), savedSubject.getId())
                        .orElse(new ClassSubjectTeacher());

                mapping.setSchoolClass(schoolClass);
                mapping.setSubject(savedSubject);
                mapping.setTeacher(teacher);

                allocationsToSave.add(mapping);
            }
        }

        pivotRepository.saveAll(allocationsToSave);
        log.info("=== DIAGNOSTIC: Successfully saved {} rows to ClassSubjectTeacher table ===", allocationsToSave.size());
    }

    private Subject mapToEntity(BulkSubjectCreateRequestDto.SubjectNodeDto dto, String standard, Subject parent, Map<Subject, Long> allocationMap) {

        Subject subject = Subject.builder()
                .subjectName(dto.getSubjectName())
                .standard(standard)
                .weeklyHours(dto.getWeeklyHours())
                .subjectType(dto.getSubjectType())
                .isAggregateOnly(dto.isAggregateOnly())
                .parentSubject(parent)
                .childSubjects(new ArrayList<>())
                .markingSchemes(new ArrayList<>())
                .build();

        if (dto.isAggregateOnly() && dto.getDefaultTeacherId() != null) {
            throw new IllegalArgumentException("Aggregate subject '" + dto.getSubjectName() + "' cannot have a default teacher.");
        }

        // Add Leaf Subjects to the allocation map
        if (!dto.isAggregateOnly() && dto.getDefaultTeacherId() != null) {
            allocationMap.put(subject, dto.getDefaultTeacherId());
        }

        // --- EXAM HANDLING ---
        if (dto.getExams() != null && !dto.getExams().isEmpty()) {
            if (dto.isAggregateOnly()) {
                throw new IllegalArgumentException("Aggregate subjects cannot have exams assigned directly.");
            }

            for (BulkSubjectCreateRequestDto.SubjectExamConfigDto examDto : dto.getExams()) {
                ExamType examType = examTypeRepository.findById(examDto.getExamTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Exam Type not found"));

                MarkingScheme scheme = MarkingScheme.builder()
                        .examType(examType)
                        .standard(standard)
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

                subject.addMarkingScheme(scheme);
            }
        }

        // --- CHILD SUBJECT HANDLING ---
        if (dto.getChildSubjects() != null && !dto.getChildSubjects().isEmpty()) {
            if (!dto.isAggregateOnly()) {
                throw new IllegalArgumentException("Only Aggregate subjects can contain child subjects.");
            }

            for (BulkSubjectCreateRequestDto.SubjectNodeDto childDto : dto.getChildSubjects()) {
                Subject childSubject = mapToEntity(childDto, standard, subject, allocationMap);
                subject.getChildSubjects().add(childSubject);
            }
        }

        return subject;
    }
}