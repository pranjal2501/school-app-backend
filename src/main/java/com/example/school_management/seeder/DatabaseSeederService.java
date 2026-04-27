package com.example.school_management.seeder;

import com.example.school_management.academics.dto.request.ClassBulkSetupRequestDto;
import com.example.school_management.academics.dto.request.StandardTemplateRequestDto;
import com.example.school_management.academics.service.AcademicSetupService;
import com.example.school_management.common.enums.Gender;
import com.example.school_management.common.enums.SubjectType;
import com.example.school_management.common.enums.AadharOwnerType;
import com.example.school_management.parent.dto.ParentCreateRequestDto;
import com.example.school_management.schoolClass.dto.ClassResponseDto;
import com.example.school_management.schoolClass.service.ClassService;
import com.example.school_management.student.dto.StudentClassAssignmentRequestDto;
import com.example.school_management.student.dto.StudentFromAdminRequestDto;
import com.example.school_management.student.dto.StudentResponseDto;
import com.example.school_management.student.service.StudentService;
import com.example.school_management.teacher.dto.TeacherFromAdminRequestDto;
import com.example.school_management.teacher.dto.response.TeacherResponseDto;
import com.example.school_management.teacher.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseSeederService {

    private final AcademicSetupService academicSetupService;
    private final TeacherService teacherService;
    private final ClassService classService;
    private final StudentService studentService;

    @Transactional
    public void executeSeeding() {
        log.info("--- STARTING DATABASE SEEDER ---");
        String adminUsername = "admin_master";

        // 🚀 FORCE SPRING SECURITY TO RECOGNIZE AN ADMIN
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        adminUsername,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        );

        try {
            // =================================================================================
            // STEP 2: SETUP CLASSES 1 TO 10 (Divisions A, B, C, D)
            // =================================================================================
            log.info("Step 2: Setting up Standards 1 to 10 with Divisions A, B, C, D...");
            List<ClassResponseDto> std7Classes = new ArrayList<>();

            for (int i = 1; i <= 10; i++) {
                ClassBulkSetupRequestDto classReq = new ClassBulkSetupRequestDto();
                classReq.setStandard(String.valueOf(i));
                classReq.setDivisions(List.of("A", "B", "C", "D"));

                List<ClassResponseDto> createdClasses = academicSetupService.setupStandardAndClasses(classReq);
                if (i == 7) {
                    std7Classes.addAll(createdClasses);
                }
            }

            // =================================================================================
            // STEP 3: CREATE 6 TEACHERS
            // =================================================================================
            log.info("Step 3: Onboarding 6 Teachers...");
            List<TeacherResponseDto> teachers = new ArrayList<>();
            String[] tFirstNames = {"Amit", "Priya", "Rahul", "Sneha", "Vikram", "Anjali"};
            String[] tLastNames = {"Sharma", "Patil", "Deshmukh", "Joshi", "Kulkarni", "Mehta"};

            for (int i = 0; i < 6; i++) {
                TeacherFromAdminRequestDto tDto = new TeacherFromAdminRequestDto();
                tDto.setFirstName(tFirstNames[i]);
                tDto.setMiddleName("Kumar");
                tDto.setLastName(tLastNames[i]);
                tDto.setEmail(tFirstNames[i].toLowerCase() + "@school.com");
                tDto.setMobile(String.format("90000000%02d", i + 1));
                tDto.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
                tDto.setDob(LocalDate.of(1985 + i, 5, 10));
                tDto.setJoiningDate(LocalDate.of(2020, 6, 1));
                tDto.setYearsOfExperienceInCurrentSchool(5);
                tDto.setAadharNo(String.format("1000000000%02d", i + 1));
                tDto.setPanNo(String.format("ABCDE%04dF", i + 1));

                teachers.add(teacherService.onboardTeacher(tDto, adminUsername));
            }

            // =================================================================================
            // STEP 4: CREATE STANDARD TEMPLATE FOR 7TH
            // =================================================================================
            log.info("Step 4: Creating Subject Template for 7th Standard...");
            StandardTemplateRequestDto templateReq = new StandardTemplateRequestDto();
            templateReq.setStandard("7");

            List<StandardTemplateRequestDto.SubjectTemplateDto> subjects = new ArrayList<>();
            String[] subNames = {"Maths", "Science", "French", "Social Science", "Marathi", "English"};
            SubjectType[] subTypes = {SubjectType.COMPULSORY, SubjectType.COMPULSORY, SubjectType.OPTIONAL,
                    SubjectType.COMPULSORY, SubjectType.COMPULSORY, SubjectType.COMPULSORY};

            for (int i = 0; i < 6; i++) {
                StandardTemplateRequestDto.SubjectTemplateDto sub = new StandardTemplateRequestDto.SubjectTemplateDto();
                sub.setSubjectName(subNames[i]);
                sub.setSubjectType(subTypes[i]);
                sub.setTheoryMarks(80);
                sub.setPracticalMarks(10);
                sub.setInternalMarks(10);
                sub.setProjectMarks(0);
                sub.setOralMarks(0);
                sub.setTotalMarks(100);
                sub.setPassingMarks(35);
                sub.setWeeklyHours(5);
                sub.setDefaultTeacherId(teachers.get(i).getId());
                subjects.add(sub);
            }
            templateReq.setSubjects(subjects);
            academicSetupService.applyTemplateToStandard(templateReq);

            // =================================================================================
            // STEP 5: ALLOCATE CLASS TEACHERS TO 7A, 7B, 7C, 7D
            // =================================================================================
            log.info("Step 5: Allocating Class Teachers to 7A, 7B, 7C, 7D...");
            for (int i = 0; i < 4 && i < std7Classes.size(); i++) {
                Long classId = std7Classes.get(i).getId();
                Long teacherId = teachers.get(i).getId();
                classService.assignClassTeacher(classId, teacherId);
            }

            // =================================================================================
            // STEP 6: CREATE 30 REAL STUDENTS FOR STD 7 (With Real Parent Names)
            // =================================================================================
            log.info("Step 6: Admitting 30 Students in Bulk...");

            // ── Real Data Arrays ──
            String[] fatherNames = {"Rajesh", "Ramesh", "Suresh", "Dinesh", "Mahesh", "Umesh", "Kiran", "Amit", "Anil", "Sunil", "Ajay", "Vijay", "Sanjay", "Manoj", "Prakash", "Ravi", "Rohan", "Sachin", "Sandeep", "Santosh", "Nitin", "Pravin", "Pramod", "Prashant", "Deepak", "Dilip", "Ashok", "Arvind", "Anand", "Vikas"};
            String[] motherNames = {"Sunita", "Anita", "Kavita", "Savita", "Sangita", "Vandana", "Archana", "Kalpana", "Ranjana", "Sujata", "Vaishali", "Sonali", "Rupali", "Manisha", "Meena", "Geeta", "Seema", "Neeta", "Rekha", "Usha", "Asha", "Lata", "Maya", "Chhaya", "Puja", "Aarti", "Swati", "Jyoti", "Smita", "Sweta"};
            String[] surnames = {"Patil", "Sharma", "Deshmukh", "Kadam", "Joshi", "Kulkarni", "Mehta", "Singh", "Verma", "Gupta", "Iyer", "Nair", "Menon", "Reddy", "Das", "Banerjee", "Chauhan", "Rajput", "Jadhav", "Pawar", "Shinde", "More", "Bhosale", "Kale", "Mahajan", "Bhat", "Rathi", "Shah", "Patel", "Kumar"};
            String[] studentNames = {"Aarav", "Aadya", "Vihaan", "Diya", "Arjun", "Saanvi", "Sai", "Ananya", "Krishna", "Myra", "Ishaan", "Kiara", "Shaurya", "Prisha", "Atharv", "Riya", "Dhruv", "Avni", "Kabir", "Meera", "Vivaan", "Pihu", "Aryan", "Navya", "Yash", "Anika", "Om", "Zara", "Reyansh", "Tara"};
            Gender[] studentGenders = {Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE};

            List<StudentFromAdminRequestDto> studentRequests = new ArrayList<>();

            for (int i = 0; i < 30; i++) {
                String mobile = String.format("80000000%02d", i + 1);
                String aadhar = String.format("2000000000%02d", i + 1);

                // 🚀 Custom Password Generation: First 3 Surname + First 3 Mobile + First 3 Aadhar
                String surPrefix = surnames[i].length() >= 3 ? surnames[i].substring(0, 3) : surnames[i];
                String mobPrefix = mobile.substring(0, 3);
                String aadPrefix = aadhar.substring(0, 3);
                String calculatedPassword = surPrefix.toUpperCase() + mobPrefix + aadPrefix;

                ParentCreateRequestDto pDto = new ParentCreateRequestDto(
                        fatherNames[i], "Kumar", surnames[i], "Business",
                        motherNames[i], "Kumari", surnames[i], "Homemaker",
                        mobile,
                        fatherNames[i].toLowerCase() + i + "@mail.com",
                        calculatedPassword, // 🚀 Passed into DTO
                        aadhar,
                        AadharOwnerType.FATHER
                );

                StudentFromAdminRequestDto sDto = StudentFromAdminRequestDto.builder()
                        .firstName(studentNames[i])
                        .middleName(fatherNames[i])
                        .lastName(surnames[i])
                        .gender(studentGenders[i])
                        .dob(LocalDate.of(2013, 1, (i % 28) + 1))
                        .aadharNo(String.format("3000000000%02d", i + 1))
                        .standard("7")
                        .parentId(null)
                        .parent(pDto)
                        .build();

                studentRequests.add(sDto);
            }

            List<StudentResponseDto> admittedStudents = studentService.admitStudentsBulk(studentRequests);

            // =================================================================================
            // STEP 7: ASSIGN ALL 30 STUDENTS TO 7A
            // =================================================================================
            log.info("Step 7: Assigning all 30 students to Division A...");
            List<Long> studentIds = admittedStudents.stream()
                    .map(StudentResponseDto::getId)
                    .collect(Collectors.toList());

            StudentClassAssignmentRequestDto assignmentReq = new StudentClassAssignmentRequestDto();
            assignmentReq.setStudentId(studentIds);
            assignmentReq.setDivision("A"); // Targets 7A

            studentService.assignClassAndDivision(assignmentReq);

            // =================================================================================
            // STEP 8: ASSIGN ROLL NUMBERS TO CLASS 7A
            // =================================================================================
            log.info("Step 8: Auto-assigning Roll Numbers to Class 7A...");

            // Safely fetch the exact Class ID for 7A
            Long class7AId = std7Classes.stream()
                    .filter(c -> "A".equalsIgnoreCase(c.getDivision()))
                    .findFirst()
                    .map(ClassResponseDto::getId)
                    .orElse(std7Classes.get(0).getId());

            classService.assignRollNumberToStudents(class7AId);

            log.info("--- DATABASE SEEDING COMPLETED SUCCESSFULLY ---");

        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}