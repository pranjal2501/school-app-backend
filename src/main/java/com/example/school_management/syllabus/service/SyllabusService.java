package com.example.school_management.syllabus.service;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.academics.repository.ClassSubjectTeacherRepository;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.subject.entity.Subject;
import com.example.school_management.subject.repository.SubjectRepository;
import com.example.school_management.syllabus.dto.SyllabusDtos.SyllabusPayload;
import com.example.school_management.syllabus.entity.Syllabus;
import com.example.school_management.syllabus.mapper.SyllabusMapper;
import com.example.school_management.syllabus.repository.SyllabusRepository;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SyllabusService {

    private final SyllabusRepository syllabusRepository;
    private final SubjectRepository subjectRepository;
    private final SyllabusMapper syllabusMapper;
    private final ClassSubjectTeacherRepository pivotRepository;
    private final RoleVerifier roleVerifier;
    private final TeacherRepository teacherRepository;

    // GET SYLLABUS
    @Transactional(readOnly = true)
    public SyllabusPayload getSyllabus(String standard, Long subjectId) {
        Syllabus syllabus = syllabusRepository.findByStandardAndSubjectId(standard, subjectId)
                .orElseThrow(() -> new RuntimeException("Syllabus not found for this Standard and Subject"));

        return syllabusMapper.toPayload(syllabus);
    }

    // CREATE OR EDIT SYLLABUS
    @Transactional
    public void saveOrUpdateSyllabus(String standard, Long subjectId, SyllabusPayload payload) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        // Fetch existing syllabus OR create a brand new empty one
        Syllabus syllabus = syllabusRepository.findByStandardAndSubjectId(standard, subjectId)
                .orElse(Syllabus.builder().standard(standard).subject(subject).build());

        // Hand off the complex merging logic to the mapper
        if (payload.getSections() != null) {
            syllabusMapper.mergeSections(syllabus, payload.getSections());
        }
        syllabusRepository.save(syllabus);
    }

    @Transactional(readOnly = true)
    public SyllabusPayload getSyllabusByAllocation(Long allocationId) {
        // 1. Authenticate Teacher
        String identifier = roleVerifier.getCurrentUsername();
        Teacher currentTeacher = teacherRepository.findByEmail(identifier)
                .or(() -> teacherRepository.findByMobile(identifier))
                .orElseThrow(() -> new AccessDeniedException("Teacher profile not found."));

        // 2. Fetch Allocation securely
        ClassSubjectTeacher allocation = pivotRepository.findById(allocationId)
                .orElseThrow(() -> new IllegalArgumentException("Allocation not found."));

        // 3. Verify Ownership
        if (!allocation.getTeacher().getId().equals(currentTeacher.getId())) {
            throw new SecurityException("Unauthorized: You are not assigned to this allocation.");
        }

        // 4. Extract standard and subject dynamically, then fetch payload
        String standard = allocation.getSchoolClass().getAcademicStructure().getStandard();
        Long subjectId = allocation.getSubject().getId();

        return getSyllabus(standard, subjectId);
    }
}