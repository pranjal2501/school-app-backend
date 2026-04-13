package com.example.school_management.teacher.service;

import com.example.school_management.common.enums.Role;
import com.example.school_management.common.exception.DuplicateResourceException;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.common.exception.UnauthorizedAccessException;
import com.example.school_management.mail.service.EmailService;
import com.example.school_management.subject.entity.Subject;
import com.example.school_management.subject.repository.SubjectRepository;
import com.example.school_management.teacher.dto.TeacherFromAdminRequestDto;
import com.example.school_management.teacher.dto.TeacherRemainingInfoDto;
import com.example.school_management.teacher.dto.TeacherResponseDto;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.teacher.mapper.TeacherMapper;
import com.example.school_management.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;
    private final PasswordEncoder passwordEncoder;
    private final SubjectRepository subjectRepository;
    private final EmailService emailService;

    private void verifyAdminOrOwnerIdentity(Teacher targetTeacher) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Authentication required to perform this action");
        }
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        // Check if the logged-in user's email matches the target teacher's email
        boolean isOwner = authentication.getName().equalsIgnoreCase(targetTeacher.getEmail());

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedAccessException("Access Denied: You do not have permission to modify this profile.");
        }
    }

    @Override
    @Transactional
    public TeacherResponseDto updateTeacherAfterOnboard(Long teacherId, TeacherRemainingInfoDto request, String username) {

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId));
        verifyAdminOrOwnerIdentity(teacher);

        teacherMapper.updateTeacherFromRemainingInfoDto(request, teacher);
        if (request.getSubjectIds() != null && !request.getSubjectIds().isEmpty()) {
            List<Subject> subjects = subjectRepository.findAllById(request.getSubjectIds());
            if (subjects.size() != request.getSubjectIds().size()) {
                throw new ResourceNotFoundException("One or more Subject IDs are invalid.");
            }
            teacher.setSubjects(subjects);
        } else if (request.getSubjectIds() != null) {
            teacher.getSubjects().clear();
        }

        Teacher updatedTeacher = teacherRepository.save(teacher);
        return teacherMapper.toResponseDto(updatedTeacher);
    }

    @Override
    @Transactional
    public TeacherResponseDto onboardTeacher(TeacherFromAdminRequestDto request, String username) {

        if (teacherRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Teacher", "email", request.getEmail());
        }
        if (teacherRepository.existsByMobile(request.getMobile())) {
            throw new DuplicateResourceException("Teacher", "mobile", request.getMobile());
        }
        if (teacherRepository.existsByAadharNo(request.getAadharNo())) {
            throw new DuplicateResourceException("Teacher", "aadharNo", request.getAadharNo());
        }
        if (teacherRepository.existsByPanNo(request.getPanNo())) {
            throw new DuplicateResourceException("Teacher", "panNo", request.getPanNo());
        }

        Teacher teacher = teacherMapper.toEntity(request);
        teacher.setRole(Role.TEACHER);
        teacher.setActive(true);
        String namePart = getSafeSubstring(teacher.getLastName(), 3).toUpperCase(Locale.ROOT);
        String mobilePart = getSafeSubstring(teacher.getMobile(), 3);
        String aadharPart = getSafeSubstring(teacher.getAadharNo(), 3);
        String rawPassword = namePart + mobilePart + aadharPart;
        teacher.setPassword(passwordEncoder.encode(rawPassword));

        Teacher savedTeacher = teacherRepository.save(teacher);

        emailService.sendTeacherOnboardingEmail(
                savedTeacher.getEmail(),
                savedTeacher.getFirstName() + " " + savedTeacher.getLastName(),
                rawPassword
        );

        return teacherMapper.toResponseDto(savedTeacher);
    }

    @Override
    @Transactional
    public List<TeacherResponseDto> onboardTeachersBulk(List<TeacherFromAdminRequestDto> requests, String username) {

        List<Teacher> teachersToSave = new ArrayList<>();
        List<String> generatedPasswords = new ArrayList<>(); // Track passwords for emails

        for (TeacherFromAdminRequestDto request : requests) {
            // 1. Validation Checks
            if (teacherRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Teacher", "email", request.getEmail());
            }
            if (teacherRepository.existsByMobile(request.getMobile())) {
                throw new DuplicateResourceException("Teacher", "mobile", request.getMobile());
            }
            if (teacherRepository.existsByAadharNo(request.getAadharNo())) {
                throw new DuplicateResourceException("Teacher", "aadharNo", request.getAadharNo());
            }
            if (teacherRepository.existsByPanNo(request.getPanNo())) {
                throw new DuplicateResourceException("Teacher", "panNo", request.getPanNo());
            }

            // 2. Mapping and Defaults
            Teacher teacher = teacherMapper.toEntity(request);
            teacher.setRole(Role.TEACHER);
            teacher.setActive(true);

            // 3. Password Generation
            String namePart = getSafeSubstring(teacher.getLastName(), 3).toUpperCase(Locale.ROOT);
            String mobilePart = getSafeSubstring(teacher.getMobile(), 3);
            String aadharPart = getSafeSubstring(teacher.getAadharNo(), 3);
            String rawPassword = namePart + mobilePart + aadharPart;

            teacher.setPassword(passwordEncoder.encode(rawPassword));

            teachersToSave.add(teacher);
            generatedPasswords.add(rawPassword);
        }

        // 4. Batch Save to Database
        List<Teacher> savedTeachers = teacherRepository.saveAll(teachersToSave);

        // 5. Dispatch Emails and Map Responses
        List<TeacherResponseDto> responses = new ArrayList<>();
        for (int i = 0; i < savedTeachers.size(); i++) {
            Teacher savedTeacher = savedTeachers.get(i);
            String rawPassword = generatedPasswords.get(i);

            emailService.sendTeacherOnboardingEmail(
                    savedTeacher.getEmail(),
                    savedTeacher.getFirstName() + " " + savedTeacher.getLastName(),
                    rawPassword
            );

            responses.add(teacherMapper.toResponseDto(savedTeacher));
        }

        return responses;
    }

    @Override
    public List<TeacherResponseDto> getAllTeachers(String username) {
        return teacherRepository.findAll().stream()
                .map(teacherMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private String getSafeSubstring(String value, int length) {
        if (value == null) return "";
        return value.length() < length ? value : value.substring(0, length);
    }
}