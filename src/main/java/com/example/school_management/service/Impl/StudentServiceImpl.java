package com.example.school_management.service.Impl;

import com.example.school_management.dto.StudentClassAssignmentRequestDto;
import com.example.school_management.dto.StudentRequestDto;
import com.example.school_management.dto.StudentResponseDto;
import com.example.school_management.entity.Student;
import com.example.school_management.exception.DuplicateResourceException;
import com.example.school_management.exception.ResourceNotFoundException;
import com.example.school_management.mapper.StudentMapper;
import com.example.school_management.repo.StudentRepository;
import com.example.school_management.service.StudentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    public StudentResponseDto admitStudent(StudentRequestDto request) {
        if (studentRepository.existsByAadharNo(request.getAadharNo())) {
            throw new DuplicateResourceException(
                    "Student with Aadhar " + request.getAadharNo() + " already exists");
        }
        Student student = studentMapper.toEntity(request);
        student.setActive(true);
        return studentMapper.toResponseDto(studentRepository.save(student));
    }

    @Override
    @Transactional // CRITICAL: Ensures all or nothing is saved
    public List<StudentResponseDto> admitStudentsBulk(List<StudentRequestDto> requests) {

        // 1. Extract all Aadhar numbers from the incoming request
        List<String> incomingAadhars = requests.stream()
                .map(StudentRequestDto::getAadharNo)
                .collect(Collectors.toList());

        // 2. Check for internal duplicates within the JSON payload itself
        long distinctAadharCount = incomingAadhars.stream().distinct().count();
        if (distinctAadharCount < requests.size()) {
            throw new RuntimeException("Duplicate Aadhar numbers found within the request payload");
        }

        // 3. Check for duplicates against the database
        List<Student> existingStudents = studentRepository.findByAadharNoIn(incomingAadhars);
        if (!existingStudents.isEmpty()) {
            String duplicateAadhars = existingStudents.stream()
                    .map(Student::getAadharNo)
                    .collect(Collectors.joining(", "));
            throw new DuplicateResourceException(
                    "Students already exist with Aadhar numbers: " + duplicateAadhars);
        }

        // 4. Map DTOs to Entities
        List<Student> studentsToSave = requests.stream().map(request -> {
            Student student = studentMapper.toEntity(request);
            student.setActive(true);
            return student;
        }).collect(Collectors.toList());

        // 5. Bulk Save
        List<Student> savedStudents = studentRepository.saveAll(studentsToSave);

        // 6. Map back to Response DTOs
        return savedStudents.stream()
                .map(studentMapper::toResponseDto)
                .collect(Collectors.toList());
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
        student.setStandard(request.getStandard());
        student.setDivision(request.getDivision());
        student.setRollNumber(request.getRollNumber());

        return studentMapper.toResponseDto(studentRepository.save(student));
    }


    private Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with ID: " + id));
    }
}