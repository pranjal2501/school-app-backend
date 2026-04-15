package com.example.school_management.student.service;


import com.example.school_management.admin.entity.Admin;
import com.example.school_management.admin.repository.AdminRepository;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.payload.StudentEmailData;
import com.example.school_management.common.enums.Gender;
import com.example.school_management.common.exception.DuplicateResourceException;
import com.example.school_management.common.exception.OutOfCapacityException;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.mail.service.EmailService;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.parent.mapper.ParentMapper;
import com.example.school_management.parent.repository.ParentRepository;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.schoolClass.repo.SchoolClassRepository;
import com.example.school_management.student.dto.*;
import com.example.school_management.student.entity.Student;
import com.example.school_management.student.mapper.StudentMapper;
import com.example.school_management.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final SchoolClassRepository classRepository;
    private final AdminRepository adminRepository;
    private final StudentMapper studentMapper;
    private final ParentRepository parentRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RoleVerifier roleVerifier;

    // Grabs the current user, ensures they are an Admin, and fetches their DB record
    private Admin verifyAndGetAdminRecord() {
        if (!roleVerifier.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Access Denied: Only Admins can perform this action.");
        }
        return adminRepository.findByUsername(roleVerifier.getCurrentUsername())
                .orElseThrow(() -> new AccessDeniedException("Admin record not found."));
    }

    private void enforceAdminOrTeacherAccess() {
        if (!roleVerifier.hasAnyRole(Role.ADMIN, Role.TEACHER)) {
            throw new AccessDeniedException("Access Denied: Operation restricted to Admins and Teachers only.");
        }
    }

    // Updated this one as well to keep the whole class consistent!
    private void enforceAdminOrParentAccess() {
        if (!roleVerifier.hasAnyRole(Role.ADMIN, Role.PARENT)) {
            throw new AccessDeniedException("Access Denied: Operation restricted to Admins and Parents only.");
        }
    }

    @Override
    @Transactional
    public StudentResponseDto admitStudent(StudentFromAdminRequestDto request) {
        Admin admin = verifyAndGetAdminRecord();

        if (studentRepository.existsByAadharNo(request.getAadharNo())) {
            throw new DuplicateResourceException(
                    "Student with Aadhaar " + request.getAadharNo() + " already exists");
        }

        Parent parent = resolveParent(request);

        String autoPassword = (request.getParentId() != null)
                ? resolveAutoPassword(parent)
                : generatePassword(parent);

        Student student = createStudent(request, parent, admin);

        student = studentRepository.save(student);

        emailService.sendAdmissionEmail(
                parent.getEmail(),
                student.getFirstName() + " " + student.getLastName(),
                parent.getEmail(),
                autoPassword
        );

        return studentMapper.toResponseDto(student);
    }

    @Override
    @Transactional
    public List<StudentResponseDto> admitStudentsBulk(List<StudentFromAdminRequestDto> requests) {

        Admin admin = verifyAndGetAdminRecord();

        Set<String> aadharNumbers = requests.stream()
                .map(StudentFromAdminRequestDto::getAadharNo)
                .collect(Collectors.toSet());

        Set<String> existingAadhars = studentRepository
                .findAllByAadharNoIn(aadharNumbers)
                .stream()
                .map(Student::getAadharNo)
                .collect(Collectors.toSet());

        List<Student> studentsToSave = new ArrayList<>();
        List<StudentEmailData> emailDataList = new ArrayList<>();

        for (StudentFromAdminRequestDto request : requests) {

            if (existingAadhars.contains(request.getAadharNo())) continue;

            Parent parent = resolveParent(request);

            String autoPassword = (request.getParentId() != null)
                    ? resolveAutoPassword(parent)
                    : generatePassword(parent);

            Student student = studentMapper.toEntity(request);
            student.setParent(parent);

            student.setAddedBy(admin);

            if (admin.getAdmittedStudents() == null) {
                admin.setAdmittedStudents(new ArrayList<>());
            }
            admin.getAdmittedStudents().add(student);

            student.setActive(true);
            studentsToSave.add(student);

            emailDataList.add(new StudentEmailData(
                    student,
                    parent.getEmail(),
                    autoPassword
            ));
        }
        List<Student> savedStudents = studentRepository.saveAll(studentsToSave);

        emailService.sendBulkAdmissionEmails(emailDataList);

        return savedStudents.stream()
                .map(studentMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDto> getAllStudents() {
        enforceAdminOrTeacherAccess();
        return studentMapper.toResponseDtoList(studentRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDto getStudentById(Long id) {
        enforceAdminOrTeacherAccess();
        return studentMapper.toResponseDto(findById(id));
    }

    @Override
    @Transactional
    public StudentResponseDto updateStudent(Long id, StudentRequestDto request) {
        verifyAndGetAdminRecord(); // Just enforcing role check

        Student existing = findById(id);
        studentMapper.updateEntity(existing, request);
        return studentMapper.toResponseDto(studentRepository.save(existing));
    }

    @Override
    @Transactional
    public StudentResponseDto updateStudentAfterAdmit(Long id, StudentRemainingInfoDto request) {
        enforceAdminOrParentAccess();

        Student existing = findById(id);
        studentMapper.updateEntityAfterAdmit(existing, request);
        return studentMapper.toResponseDto(studentRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        verifyAndGetAdminRecord(); // Just enforcing role check

        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with ID: " + id);
        }
        studentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDropdownDto> searchStudents(String standard, String division, String query) {
        enforceAdminOrTeacherAccess();

        String safeQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : "";
        List<Student> students = studentRepository.searchStudentsForComplaint(standard, division, safeQuery);

        return students.stream()
                .map(studentMapper::toDropdownDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<StudentResponseDto> assignClassAndDivision(StudentClassAssignmentRequestDto request) {
        verifyAndGetAdminRecord(); // Enforcing Admin Role

        List<Student> addedStudent=new ArrayList<>();
        for(Long id: request.getStudentId()) {
            Student student = findById(id);

            String std = student.getStandard().toString();
            String className = std.substring(std.indexOf('_') + 1) + "-" + request.getDivision();

            SchoolClass assignedClass = classRepository.findByClassName(className).orElseThrow(() -> new ResourceNotFoundException("Class " + className + " don't exist"));
            SchoolClass previousClass = student.getAssignedClass();

            Gender gender = student.getGender();

            if (gender.equals(Gender.MALE)) {
                if (assignedClass.getCurrentBoys() == assignedClass.getBoys()) {
                    throw new OutOfCapacityException("Boys capacity in class " + className + " already fulfilled");
                }
            } else if (gender.equals(Gender.FEMALE)) {
                if (assignedClass.getCurrentGirls() == assignedClass.getGirls()) {
                    throw new OutOfCapacityException("Girls capacity in class " + className + " already fulfilled");
                }
            }

            if (previousClass != null) {
                if (gender.equals(Gender.MALE)) {
                    previousClass.setCurrentBoys(previousClass.getCurrentBoys() - 1);
                } else if (gender.equals(Gender.FEMALE)) {
                    previousClass.setCurrentGirls(previousClass.getCurrentGirls() - 1);
                }
                classRepository.save(previousClass);
            }

            if (gender.equals(Gender.MALE)) {
                assignedClass.setCurrentBoys(assignedClass.getCurrentBoys() + 1);
            } else if (gender.equals(Gender.FEMALE)) {
                assignedClass.setCurrentGirls(assignedClass.getCurrentGirls() + 1);
            }

            student.setAssignedClass(assignedClass);
            addedStudent.add(student);
            studentRepository.save(student);
        }
        return studentMapper.toResponseDtoList(addedStudent);
    }

    private Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with ID: " + id));
    }

    // --------------HELPERS-----------------

    private Parent resolveParent(StudentFromAdminRequestDto request) {
        if (request.getParent() == null && request.getParentId() == null) {
            throw new IllegalArgumentException("Either parentId or parent must be provided");
        }

        if (request.getParentId() != null) {
            return parentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent not found"));
        }

        Parent parent = ParentMapper.toEntity(request.getParent());

        String password = generatePassword(parent);
        parent.setPassword(passwordEncoder.encode(password));

        return parentRepository.save(parent);
    }

    private String generatePassword(Parent parent) {
        return safe(parent.getFatherLastName()).toUpperCase() +
                safe(parent.getMobilePrimary()) +
                safe(parent.getAadharNo());
    }

    private String safe(String value) {
        return value.length() >= 3 ? value.substring(0, 3) : value;
    }

    private String resolveAutoPassword(Parent parent) {
        String tempPassword = generatePassword(parent);

        if (passwordEncoder.matches(tempPassword, parent.getPassword())) {
            return tempPassword;
        }

        return "Use existing password";
    }

    private Student createStudent(StudentFromAdminRequestDto request, Parent parent, Admin admin) {
        Student student = studentMapper.toEntity(request);
        student.setParent(parent);
        student.setAddedBy(admin);
        student.setActive(true);
        return student;
    }
}