package com.example.school_management.student.mapper;

import com.example.school_management.parent.entity.Parent;
import com.example.school_management.parent.dto.ParentResponseDto;
import com.example.school_management.student.dto.*;
import com.example.school_management.student.entity.Student;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StudentMapper {

    private final PasswordEncoder passwordEncoder;

    public Student toEntity(StudentRequestDto dto) {

        return Student.builder()
                .firstName(dto.getFirstName())
                .middleName(dto.getMiddleName())
                .lastName(dto.getLastName())
                .gender(dto.getGender())
                .dob(dto.getDob())
                .bloodGroup(dto.getBloodGroup())
                .aadharNo(dto.getAadharNo())
                .nationality(dto.getNationality())
                .religion(dto.getReligion())
                .category(dto.getCategory())
                .rollNumber(dto.getRollNumber())
                .previousSchool(dto.getPreviousSchool())
                .previousPercentage(dto.getPreviousPercentage())
                .birthCertificateUrl(dto.getBirthCertificateUrl())
                .aadharPhotoUrl(dto.getAadharPhotoUrl())
                .previousMarksheetUrl(dto.getPreviousMarksheetUrl())
                .passportPhotoUrl(dto.getPassportPhotoUrl())
                .leavingCertificateUrl(dto.getLeavingCertificateUrl())
                .casteCertificateUrl(dto.getCasteCertificateUrl())
                .incomeCertificateUrl(dto.getIncomeCertificateUrl())
                .build();
    }

    // StudentRequestDto → Existing Student entity (for update)
    public void updateEntity(Student student, StudentRequestDto dto) {
        student.setFirstName(dto.getFirstName());
        student.setMiddleName(dto.getMiddleName());
        student.setLastName(dto.getLastName());
        student.setGender(dto.getGender());
        student.setDob(dto.getDob());
        student.setBloodGroup(dto.getBloodGroup());
        student.setNationality(dto.getNationality());
        student.setReligion(dto.getReligion());
        student.setCategory(dto.getCategory());
        student.setRollNumber(dto.getRollNumber());

        Parent parent = dto.getParent() != null ? dto.getParent() : new Parent();

        if (dto.getParent().getPassword() != null && !dto.getParent().getPassword().isBlank()) {
            parent.setPassword(passwordEncoder.encode(dto.getParent().getPassword()));
        }

        student.setParent(parent);

        student.setPreviousSchool(dto.getPreviousSchool());
        student.setPreviousPercentage(dto.getPreviousPercentage());
        student.setBirthCertificateUrl(dto.getBirthCertificateUrl());
        student.setAadharPhotoUrl(dto.getAadharPhotoUrl());
        student.setPreviousMarksheetUrl(dto.getPreviousMarksheetUrl());
        student.setPassportPhotoUrl(dto.getPassportPhotoUrl());
        student.setLeavingCertificateUrl(dto.getLeavingCertificateUrl());
        student.setCasteCertificateUrl(dto.getCasteCertificateUrl());
        student.setIncomeCertificateUrl(dto.getIncomeCertificateUrl());
    }


    public StudentResponseDto toResponseDto(Student student) {
        StudentResponseDto dto = new StudentResponseDto();

        dto.setId(student.getId());
        dto.setFirstName(student.getFirstName());
        dto.setMiddleName(student.getMiddleName());
        dto.setLastName(student.getLastName());
        dto.setStandard(student.getStandard());
        dto.setRollNumber(student.getRollNumber());
        dto.setAadharNo(student.getAadharNo());
        dto.setGender(student.getGender());
        dto.setDob(student.getDob());
        dto.setBloodGroup(student.getBloodGroup());
        dto.setNationality(student.getNationality());
        dto.setReligion(student.getReligion());
        dto.setCategory(student.getCategory());
        // 1. Check if the parent actually exists to avoid NullPointerExceptions
        if (student.getParent() != null) {

            // 2. Extract the raw entity
            ParentResponseDto parentDto = getParentResponseDto(student);

            // 5. Finally, attach the formatted DTO to your StudentResponseDto
            dto.setParent(parentDto);
        }
        dto.setPreviousSchool(student.getPreviousSchool());
        dto.setPreviousPercentage(student.getPreviousPercentage());
        dto.setActive(student.isActive());

        return dto;
    }

    @Nonnull
    private static ParentResponseDto getParentResponseDto(Student student) {
        Parent p = student.getParent();

        // 3. Create the new DTO
        ParentResponseDto parentDto = new ParentResponseDto();

        // 4. Manually map the fields you want to expose
        parentDto.setId(p.getId());
        parentDto.setFatherFirstName(p.getFatherFirstName());
        parentDto.setFatherMiddleName(p.getFatherMiddleName());
        parentDto.setFatherLastName(p.getFatherLastName());
        parentDto.setFatherOccupation(p.getFatherOccupation());

        parentDto.setMotherFirstName(p.getMotherFirstName());
        parentDto.setMotherLastName(p.getMotherLastName());
        parentDto.setMotherOccupation(p.getMotherOccupation());

        parentDto.setGuardianFirstName(p.getGuardianFirstName());
        parentDto.setGuardianMiddleName(p.getGuardianMiddleName());
        parentDto.setGuardianLastName(p.getGuardianLastName());

        parentDto.setMobilePrimary(p.getMobilePrimary());
        parentDto.setEmail(p.getEmail());
        parentDto.setAadharNo(p.getAadharNo());
        return parentDto;
    }

    // List<Student> → List<StudentResponseDto>
    public List<StudentResponseDto> toResponseDtoList(List<Student> students) {
        return students.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    private Parent buildParent(StudentRequestDto dto) {
        Parent parent = dto.getParent();

        if (parent == null) {
            throw new IllegalArgumentException("Parent is required");
        }

        if (parent.getPassword() == null || parent.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required for Parent account");
        }

        parent.setPassword(passwordEncoder.encode(parent.getPassword()));
        return parent;
    }

    public Student toEntity(StudentFromAdminRequestDto dto) {

        return Student.builder()
                .firstName(dto.getFirstName())
                .middleName(dto.getMiddleName())
                .lastName(dto.getLastName())
                .gender(dto.getGender())
                .dob(dto.getDob())
                .aadharNo(dto.getAadharNo())
                .standard(dto.getStandard())
                .build();
    }

    public void updateEntityAfterAdmit(Student student, StudentRemainingInfoDto dto) {
        student.setGender(dto.getGender());
        student.setBloodGroup(dto.getBloodGroup());
        student.setNationality(dto.getNationality());
        student.setReligion(dto.getReligion());
        student.setCategory(dto.getCategory());
        student.setPreviousSchool(dto.getPreviousSchool());
        student.setPreviousPercentage(dto.getPreviousPercentage());
        student.setBirthCertificateUrl(dto.getBirthCertificateUrl());
        student.setAadharPhotoUrl(dto.getAadharPhotoUrl());
        student.setPreviousMarksheetUrl(dto.getPreviousMarksheetUrl());
        student.setPassportPhotoUrl(dto.getPassportPhotoUrl());
        student.setLeavingCertificateUrl(dto.getLeavingCertificateUrl());
        student.setCasteCertificateUrl(dto.getCasteCertificateUrl());
        student.setIncomeCertificateUrl(dto.getIncomeCertificateUrl());
    }

    public StudentDropdownDto toDropdownDto(Student student) {
        if (student == null) {
            return null;
        }

        StudentDropdownDto dto = new StudentDropdownDto();
        dto.setId(student.getId());

        // Combine first and last name for easy frontend display
        String fullName = student.getFirstName();
        if (student.getLastName() != null) {
            fullName += " " + student.getLastName();
        }
        dto.setFullName(fullName);

        dto.setRollNumber(String.valueOf(student.getRollNumber()));

        return dto;
    }

}