package com.example.school_management.syllabus.service;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.academics.repository.ClassSubjectTeacherRepository;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.student.repository.StudentRepository;
import com.example.school_management.syllabus.entity.SyllabusProgress;
import com.example.school_management.syllabus.repository.SyllabusProgressRepository;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SyllabusProgressService {

    private final SyllabusProgressRepository progressRepository;
    private final ClassSubjectTeacherRepository allocationRepository;
    private final SyllabusNodeStrategy nodeStrategy;

    // Injected dependencies moved from the Controller
    private final RoleVerifier roleVerifier;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    // ─── ADMIN FETCH PROGRESS ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Boolean> getProgressMap(Long allocationId) {
        List<SyllabusProgress> progressList = progressRepository.findAllByAllocationId(allocationId);
        return progressList.stream()
                .filter(SyllabusProgress::isCompleted)
                .collect(Collectors.toMap(SyllabusProgress::getNodePath, SyllabusProgress::isCompleted));
    }

    // ─── PARENT FETCH PROGRESS ─────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Boolean> getProgressForParent(Long allocationId) {
        String identifier = roleVerifier.getCurrentUsername();

        ClassSubjectTeacher allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new IllegalArgumentException("Allocation ID " + allocationId + " not found."));

        boolean hasStudentInClass = studentRepository.existsByParentIdentifierAndClassId(
                identifier,
                allocation.getSchoolClass().getId()
        );

        if (!hasStudentInClass) {
            throw new AccessDeniedException("Unauthorized: Your child is not enrolled in this class.");
        }

        return getProgressMap(allocationId);
    }

    // ─── TEACHER TOGGLE PROGRESS ──────────────────────────────────────────────

    @Transactional
    public void toggleProgressAsTeacher(Long allocationId, String nodePath, boolean isComplete) {
        String identifier = roleVerifier.getCurrentUsername();
        Teacher currentTeacher = teacherRepository.findByEmail(identifier)
                .or(() -> teacherRepository.findByMobile(identifier))
                .orElseThrow(() -> new AccessDeniedException("Teacher profile not found."));

        ClassSubjectTeacher allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new IllegalArgumentException("Allocation not found."));

        if (!allocation.getTeacher().getId().equals(currentTeacher.getId())) {
            throw new AccessDeniedException("Unauthorized: You cannot modify progress for another teacher's class.");
        }

        // Delegate to the internal toggle logic
        toggleNode(allocationId, nodePath, isComplete);
    }

    // ─── TEACHER FETCH PROGRESS ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Boolean> getProgressForTeacher(Long allocationId) {
        String identifier = roleVerifier.getCurrentUsername();
        Teacher currentTeacher = teacherRepository.findByEmail(identifier)
                .or(() -> teacherRepository.findByMobile(identifier))
                .orElseThrow(() -> new AccessDeniedException("Teacher profile not found."));

        ClassSubjectTeacher allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new IllegalArgumentException("Allocation not found."));

        if (!allocation.getTeacher().getId().equals(currentTeacher.getId())) {
            throw new AccessDeniedException("Unauthorized: You cannot access progress for another teacher's class.");
        }

        return getProgressMap(allocationId);
    }

    // ─── INTERNAL TOGGLE LOGIC (THE BRAIN) ────────────────────────────────────

    private void toggleNode(Long allocationId, String nodePath, boolean isComplete) {
        markSpecificNode(allocationId, nodePath, isComplete);

        if (isComplete) {
            checkAndCompleteParent(allocationId, nodePath);
        } else {
            invalidateAncestors(allocationId, nodePath);
        }
    }

    private void markSpecificNode(Long allocationId, String nodePath, boolean isComplete) {
        ClassSubjectTeacher allocation = allocationRepository.getReferenceById(allocationId);

        SyllabusProgress progress = progressRepository.findByAllocationIdAndNodePath(allocationId, nodePath)
                .orElse(SyllabusProgress.builder()
                        .allocation(allocation)
                        .nodePath(nodePath)
                        .build());

        progress.setCompleted(isComplete);
        progressRepository.save(progress);
    }

    private void checkAndCompleteParent(Long allocationId, String childNodePath) {
        int lastSlashIndex = childNodePath.lastIndexOf('/');
        if (lastSlashIndex == -1) return;

        String parentPath = childNodePath.substring(0, lastSlashIndex);
        long totalChildrenInDefinition = nodeStrategy.countTotalChildren(parentPath);
        long completedChildrenCount = progressRepository.countCompletedImmediateChildren(allocationId, parentPath);

        if (completedChildrenCount == totalChildrenInDefinition) {
            markSpecificNode(allocationId, parentPath, true);
            checkAndCompleteParent(allocationId, parentPath);
        }
    }

    private void invalidateAncestors(Long allocationId, String childNodePath) {
        int lastSlashIndex = childNodePath.lastIndexOf('/');
        if (lastSlashIndex == -1) return;

        String parentPath = childNodePath.substring(0, lastSlashIndex);

        markSpecificNode(allocationId, parentPath, false);
        invalidateAncestors(allocationId, parentPath);
    }
}