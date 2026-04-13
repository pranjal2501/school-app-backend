package com.example.school_management.schoolClass.serviceImpl;

import com.example.school_management.common.enums.Gender;
import com.example.school_management.schoolClass.dto.ClassRequestDto;
import com.example.school_management.schoolClass.dto.ClassResponseDto;
import com.example.school_management.schoolClass.dto.StudentRollNoResponseDto;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.schoolClass.mapper.ClassMapper;
import com.example.school_management.schoolClass.repo.SchoolClassRepository;
import com.example.school_management.schoolClass.service.ClassService;
import com.example.school_management.student.entity.Student;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.common.exception.DuplicateResourceException;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.common.exception.UnauthorizedAccessException;
import com.example.school_management.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final SchoolClassRepository classRepository;
    private final TeacherRepository teacherRepository;
    private final ClassMapper classMapper;

    private void verifyAdminIdentity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Authentication required to perform this action");
        }
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new UnauthorizedAccessException("Identity Verification Failed: Only administrators can execute this operation");
        }
    }

    @Override
    @Transactional
    public ClassResponseDto createClass(ClassRequestDto request) {
        verifyAdminIdentity();

        String className = request.getStandard() + "-" + request.getDivision();

        SchoolClass classEntity = new SchoolClass();
        classEntity.setClassName(className);
        classEntity.setGirls(request.getGirls());
        classEntity.setBoys(request.getBoys());
        classEntity.setCurrentBoys(0);
        classEntity.setCurrentGirls(0);
        classEntity.setTotalStudents(request.getTotalStudents());
        classEntity.setDivision(request.getDivision());
        classEntity.setStandard(request.getStandard());

        return classMapper.toResponseDto(classRepository.save(classEntity));
    }

    @Override
    @Transactional
    public List<ClassResponseDto> createClassesBulk(List<ClassRequestDto> requests) {
        verifyAdminIdentity();

        List<SchoolClass> classesToSave = new ArrayList<>();

        // Convert all DTOs to Entities
        for (ClassRequestDto request : requests) {
            String className = request.getStandard().getValue() + "-" + request.getDivision().name();

            SchoolClass classEntity = new SchoolClass();
            classEntity.setClassName(className);
            classEntity.setGirls(request.getGirls());
            classEntity.setBoys(request.getBoys());
            classEntity.setCurrentBoys(0);     // Defaulting to 0 on creation
            classEntity.setCurrentGirls(0);    // Defaulting to 0 on creation
            classEntity.setTotalStudents(request.getTotalStudents());
            classEntity.setDivision(request.getDivision());
            classEntity.setStandard(request.getStandard());

            classesToSave.add(classEntity);
        }

        // Save all entities in a single batch
        List<SchoolClass> savedClasses = classRepository.saveAll(classesToSave);

        // Convert the saved entities back to DTOs
        return savedClasses.stream()
                .map(classMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ClassResponseDto> getAllClasses() {
        verifyAdminIdentity();
        return classMapper.toResponseDtoList(classRepository.findAll());
    }

    @Override
    public ClassResponseDto getClassById(Long id) {
        verifyAdminIdentity();
        SchoolClass classEntity = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", id));
        return classMapper.toResponseDto(classEntity);
    }

    @Override
    @Transactional
    public ClassResponseDto updateClass(Long id, ClassRequestDto request) {
        verifyAdminIdentity();

        SchoolClass existing = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", id));

        existing.setStandard(request.getStandard());
        existing.setDivision(request.getDivision());
        existing.setTotalStudents(request.getTotalStudents());
        existing.setBoys(request.getBoys());
        existing.setGirls(request.getGirls());

        String className = existing.getStandard() + "-" + existing.getDivision();
        existing.setClassName(className);

        return classMapper.toResponseDto(classRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteClass(Long id) {
        verifyAdminIdentity();
        if (!classRepository.existsById(id)) {
            throw new ResourceNotFoundException("Class", "id", id);
        }
        classRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ClassResponseDto assignClassTeacher(Long classId, Long teacherId) {
        verifyAdminIdentity();

        SchoolClass classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", classId));

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId));

        Optional<SchoolClass> existingAssignment = classRepository.findByClassTeacher(teacher);

        if (existingAssignment.isPresent()) {
            if (existingAssignment.get().getId().equals(classId)) {
                throw new DuplicateResourceException("This teacher is already assigned to this specific class.");
            }
            throw new IllegalArgumentException(
                    "Teacher is already assigned as Class Teacher to " + existingAssignment.get().getClassName()
            );
        }

        classEntity.setClassTeacher(teacher);
        return classMapper.toResponseDto(classRepository.save(classEntity));
    }

    @Override
    @Transactional
    public List<String> assignRollNumberToStudents(Long classId)
    {
        SchoolClass current_class=classRepository.findById(classId).orElseThrow(() -> new ResourceNotFoundException("Class not found"));;
        List<Student> studentList=current_class.getStudents()
            .stream()
            .filter(Student::isActive)
            .sorted(Comparator
                    .comparing((Student s) -> s.getGender() == Gender.MALE ? 0 : 1)  // boys first
                    .thenComparing(Student::getFirstName)                              // then A-Z by first name
                    .thenComparing(Student::getLastName))                              // tie-break by last name
            .toList();

        List<String> response= new ArrayList<>();
        for (int i = 0; i < studentList.size(); i++) {
            studentList.get(i).setRollNumber(i + 1);
            response.add(studentList.get(i).getFirstName()+" is assigned with "+ i+1);
        }

        return response;
    }

    @Override
    public List<StudentRollNoResponseDto> getSTudentListWIthRollNum(Long classId)
    {
        SchoolClass currentClass= classRepository.findById(classId).orElseThrow(()-> new ResourceNotFoundException("Class not found"));

        return currentClass.getStudents()
                .stream()
                .filter(Student::isActive)
                .filter(s -> s.getRollNumber() != null)  // only students with roll assigned
                .sorted(Comparator.comparing(Student::getRollNumber))
                .map(s -> new StudentRollNoResponseDto(
                        s.getRollNumber(),
                        s.getFirstName(),
                        s.getLastName(),
                        s.getGender().name(),
                        s.getRegId()
                ))
                .toList();
    }
}