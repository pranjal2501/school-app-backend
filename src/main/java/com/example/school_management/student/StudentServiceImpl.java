package com.example.school_management.student;

import com.example.school_management.schoolClass.Class;
import com.example.school_management.parent.Parent;
import com.example.school_management.common.enums.Gender;
import com.example.school_management.common.exception.DuplicateResourceException;
import com.example.school_management.common.exception.OutOfCapacityException;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.schoolClass.ClassRepository;
import com.example.school_management.parent.ParentRepository;
import com.example.school_management.service.EmailService;
import com.example.school_management.student.dto.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;
    private final StudentMapper studentMapper;
    private final ParentRepository parentRepository;

    @Override
    public StudentResponseDto admitStudent(StudentFromAdminRequestDto request) {

        Parent parent;

        if (studentRepository.existsByAadharNo(request.getAadharNo())) {
            throw new DuplicateResourceException(
                    "Student with Aadhar " + request.getAadharNo() + " already exists");
        }

        if(request.getParent()==null && request.getParentId()==null)
        {throw new IllegalArgumentException("Either parentId or parent must be provided");}

        if(request.getParentId()!=null)
        {
            parent= parentRepository.findById(request.getParentId()).orElseThrow(() -> new RuntimeException("Parent not found"));
        }else
        {   parent= request.getParent();
            String autoPassword=parent.getFatherLastName().substring(0,3)+parent.getMobilePrimary().substring(0,3)+parent.getAadharNo().substring(0,3);

            parent.setPassword(autoPassword);
            parent = parentRepository.save(parent);
        }

        Student student = studentMapper.toEntity(request);

        student.setParent(parent);

        student.setActive(true);

        EmailService.sendAdmissionEmail(
                parent.getEmail(),
                student.getFirstName() + " " + student.getLastName(),
                parent.getEmail(),
                parent.getPassword()
        );
        return studentMapper.toResponseDto(studentRepository.save(student));
    }

    @Override
    public StudentResponseDto updateStudentAfterAdmit(Long id, StudentRemainingInfoDto request)
    {

        Student existing = findById(id);
        studentMapper.updateEntityAfterAdmit(existing, request);
        return studentMapper.toResponseDto(studentRepository.save(existing));
    }

    @Override
    @Transactional // CRITICAL: Ensures all or nothing is saved
    public List<StudentResponseDto> admitStudentsBulk(List<StudentFromAdminRequestDto> requests) {

//        // 1. Extract all Aadhar numbers from the incoming request
//        List<String> incomingAadhars = requests.stream()
//                .map(StudentFromAdminRequestDto::getAadharNo)
//                .collect(Collectors.toList());
//
//        // 2. Check for internal duplicates within the JSON payload itself
//        long distinctAadharCount = incomingAadhars.stream().distinct().count();
//        if (distinctAadharCount < requests.size()) {
//            throw new RuntimeException("Duplicate Aadhar numbers found within the request payload");
//        }
//
//        // 3. Check for duplicates against the database
//        List<Student> existingStudents = studentRepository.findByAadharNoIn(incomingAadhars);
//        if (!existingStudents.isEmpty()) {
//            String duplicateAadhars = existingStudents.stream()
//                    .map(Student::getAadharNo)
//                    .collect(Collectors.joining(", "));
//            throw new DuplicateResourceException(
//                    "Students already exist with Aadhar numbers: " + duplicateAadhars);
//        }
//
//        // 4. Map DTOs to Entities
//        List<Student> studentsToSave = requests.stream().map(request -> {
//            Student student = studentMapper.toEntity(request);
//            student.setActive(true);
//            return student;
//        }).collect(Collectors.toList());
//
//        // 5. Bulk Save
//        List<Student> savedStudents = studentRepository.saveAll(studentsToSave);
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

        // 6. Map back to Response DTOs
//        return savedStudents.stream()
//                .map(studentMapper::toResponseDto)
//                .collect(Collectors.toList());
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
            if(assignedClass.getCurrentBoys()==0)
            {throw new OutOfCapacityException("Boys capacity in class "+className+" already fulfilled");}
        }
        else if (gender.equals(Gender.FEMALE)) {
            if(assignedClass.getCurrentGirls()==0)
            {throw new OutOfCapacityException("Girls capacity in class "+className+" already fulfilled");}
        }

        if (previousClass != null) {
            if (gender.equals(Gender.MALE)) {
                previousClass.setCurrentBoys(previousClass.getCurrentBoys() + 1);
            } else if (gender.equals(Gender.FEMALE)) {
                previousClass.setCurrentGirls(previousClass.getCurrentGirls() + 1);
            }
            classRepository.save(previousClass);
        }

        if(gender.equals(Gender.MALE)) {
            assignedClass.setCurrentBoys(assignedClass.getCurrentBoys() - 1);
        } else if (gender.equals(Gender.FEMALE)) {
            assignedClass.setCurrentGirls(assignedClass.getCurrentGirls() - 1);
        }

        student.setRollNumber(request.getRollNumber());
        student.setAssignedClass(assignedClass);

        return studentMapper.toResponseDto(studentRepository.save(student));
    }


    private Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with ID: " + id));
    }
}