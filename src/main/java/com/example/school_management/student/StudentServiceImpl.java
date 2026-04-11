package com.example.school_management.student;

import com.example.school_management.common.enums.Division;
import com.example.school_management.common.enums.Standard;
import com.example.school_management.schoolClass.Class;
import com.example.school_management.parent.Parent;
import com.example.school_management.common.enums.Gender;
import com.example.school_management.common.exception.DuplicateResourceException;
import com.example.school_management.common.exception.OutOfCapacityException;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.schoolClass.ClassRepository;
import com.example.school_management.parent.ParentRepository;
import com.example.school_management.mail.EmailService;
import com.example.school_management.student.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;
    private final StudentMapper studentMapper;
    private final ParentRepository parentRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public StudentResponseDto admitStudent(StudentFromAdminRequestDto request) {

        Parent parent;
        String autoPassword=null;

        if (studentRepository.existsByAadharNo(request.getAadharNo())) {
            throw new DuplicateResourceException(
                    "Student with Aadhar " + request.getAadharNo() + " already exists");
        }

        if(request.getParent()==null && request.getParentId()==null)
        {throw new IllegalArgumentException("Either parentId or parent must be provided");}

        if(request.getParentId()!=null)
        {
            parent= parentRepository.findById(request.getParentId()).orElseThrow(() -> new RuntimeException("Parent not found"));
            String tempPassword=parent.getFatherLastName().substring(0,3).toUpperCase(Locale.ROOT)+parent.getMobilePrimary().substring(0,3)+parent.getAadharNo().substring(0,3);

            if(passwordEncoder.matches(tempPassword, parent.getPassword())){
                autoPassword=tempPassword;
            }
        }else{
            parent= request.getParent();
            autoPassword=parent.getFatherLastName().substring(0,3).toUpperCase(Locale.ROOT)+parent.getMobilePrimary().substring(0,3)+parent.getAadharNo().substring(0,3);
            parent.setPassword(passwordEncoder.encode(autoPassword));
            parent = parentRepository.save(parent);
        }

        Student student = studentMapper.toEntity(request);

        student.setParent(parent);
        student.setActive(true);

        student=studentRepository.save(student);

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
    public List<StudentResponseDto> admitStudentsBulk(List<StudentFromAdminRequestDto> requests) {

         List<StudentResponseDto> savedStudents = new ArrayList<>();

        for(StudentFromAdminRequestDto s : requests)
        {
            boolean already=studentRepository.findByAadharNo(s.getAadharNo()).isPresent();
            if (already) {
                System.out.println("Student already exists");
            } else {
                savedStudents.add(admitStudent(s));
            }

        }
        return savedStudents;
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
    public StudentResponseDto getStudentByRollNumber(Integer rollNumber) {
        Student student = studentRepository.findByRollNumber(rollNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with Roll Number: " + rollNumber));
        return studentMapper.toResponseDto(student);
    }

    @Override
    public StudentResponseDto updateStudent(Long id, StudentRequestDto request) {
        Student existing = findById(id);
        studentMapper.updateEntity(existing, request);
        return studentMapper.toResponseDto(studentRepository.save(existing));
    }

    @Override
    public void toggleStudentStatus(Long id) {
        Student student = findById(id);
        student.setActive(!student.isActive());
        studentRepository.save(student);
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

        Class assignedClass = classRepository.findByClassName(className).orElseThrow(()->new ResourceNotFoundException("Class "+className+" don't exist"));
        Class previousClass = student.getAssignedClass();

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
}