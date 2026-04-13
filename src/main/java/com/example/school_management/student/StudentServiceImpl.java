package com.example.school_management.student;

import com.example.school_management.admin.Admin;
import com.example.school_management.admin.AdminRepository;
import com.example.school_management.common.enums.Division;
import com.example.school_management.common.enums.Standard;
import com.example.school_management.common.payload.StudentEmailData;
import com.example.school_management.parent.ParentMapper;
import com.example.school_management.parent.dto.ParentCreateRequestDto;
import com.example.school_management.parent.Parent;
import com.example.school_management.common.enums.Gender;
import com.example.school_management.common.exception.DuplicateResourceException;
import com.example.school_management.common.exception.OutOfCapacityException;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.parent.ParentRepository;
import com.example.school_management.mail.EmailService;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.schoolClass.repo.SchoolClassRepository;
import com.example.school_management.student.dto.*;
import lombok.RequiredArgsConstructor;
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

    @Override
    @Transactional
    public StudentResponseDto admitStudent(StudentFromAdminRequestDto request, String adminName) {

        if (studentRepository.existsByAadharNo(request.getAadharNo())) {
            throw new DuplicateResourceException(
                    "Student with Aadhaar " + request.getAadharNo() + " already exists");
        }

        Admin admin = adminRepository.findByUsername(adminName)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

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
    public StudentResponseDto updateStudentAfterAdmit(Long id, StudentRemainingInfoDto request)
    {

        Student existing = findById(id);
        studentMapper.updateEntityAfterAdmit(existing, request);
        return studentMapper.toResponseDto(studentRepository.save(existing));
    }

    @Override
    @Transactional(readOnly = true) // Crucial for performance on GET requests
    public List<StudentDropdownDto> searchStudents(Standard standard, Division division, String query) {

        // 1. Sanitize the input to prevent whitespace query errors
        String safeQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : "";

        // 2. Fetch the raw entities from the database
        List<Student> students = studentRepository.searchStudentsForComplaint(standard, division, safeQuery);

        // 3. Strictly delegate to the Mapper to transform Entities -> lightweight DTOs
        return students.stream()
                .map(studentMapper::toDropdownDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<StudentResponseDto> admitStudentsBulk(
            List<StudentFromAdminRequestDto> requests,
            String adminName
    ) {

        Admin admin = adminRepository.findByUsername(adminName)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

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

            // 1. Set the owning side (Student -> Admin)
            student.setAddedBy(admin);

            // 2. Set the inverse side (Admin -> Student)
            // Safety check to prevent NullPointerExceptions
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
    public List<StudentResponseDto> getAllStudents() {
        return studentMapper.toResponseDtoList(studentRepository.findAll());
    }

    @Override
    public StudentResponseDto getStudentById(Long id) {
        return studentMapper.toResponseDto(findById(id));
    }


    @Override
    public StudentResponseDto updateStudent(Long id, StudentRequestDto request) {
        Student existing = findById(id);
        studentMapper.updateEntity(existing, request);
        return studentMapper.toResponseDto(studentRepository.save(existing));
    }


    @Override
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with ID: " + id);
        }
        studentRepository.deleteById(id);
    }

    @Override
    public StudentResponseDto assignClassAndDivision(Long id, StudentClassAssignmentRequestDto request) {
        Student student=findById(id);

        String className = request.getStandard() + "-" + request.getDivision();

        SchoolClass assignedClass = classRepository.findByClassName(className).orElseThrow(()->new ResourceNotFoundException("Class "+className+" don't exist"));
        SchoolClass previousClass = student.getAssignedClass();

        Gender gender= student.getGender();

        if(gender.equals(Gender.MALE))
        {
            if(assignedClass.getCurrentBoys()==assignedClass.getBoys())
            {throw new OutOfCapacityException("Boys capacity in class "+className+" already fulfilled");}
        }
        else if (gender.equals(Gender.FEMALE)) {
            if(assignedClass.getCurrentGirls()== assignedClass.getGirls())
            {throw new OutOfCapacityException("Girls capacity in class "+className+" already fulfilled");}
        }

        if (previousClass != null) {
            if (gender.equals(Gender.MALE)) {
                previousClass.setCurrentBoys(previousClass.getCurrentBoys() - 1);
            } else if (gender.equals(Gender.FEMALE)) {
                previousClass.setCurrentGirls(previousClass.getCurrentGirls() - 1);
            }
            classRepository.save(previousClass);
        }

        if(gender.equals(Gender.MALE)) {
            assignedClass.setCurrentBoys(assignedClass.getCurrentBoys() + 1);
        } else if (gender.equals(Gender.FEMALE)) {
            assignedClass.setCurrentGirls(assignedClass.getCurrentGirls() + 1);
        }

        student.setAssignedClass(assignedClass);

        return studentMapper.toResponseDto(studentRepository.save(student));
    }


    private Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with ID: " + id));
    }


    //SUPPORTING METHODS, NOT INTENDED TO BE USED FOR ENDPOINTS

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

        return parentRepository.save(parent); // ✅ save ONCE
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