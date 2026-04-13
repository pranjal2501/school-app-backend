package com.example.school_management.student;

import com.example.school_management.parent.Parent;
import com.example.school_management.student.dto.*;
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
        dto.setRollNumber(student.getRollNumber());
        dto.setAadharNo(student.getAadharNo());
        dto.setGender(student.getGender());
        dto.setDob(student.getDob());
        dto.setBloodGroup(student.getBloodGroup());
        dto.setNationality(student.getNationality());
        dto.setReligion(student.getReligion());
        dto.setCategory(student.getCategory());
        dto.setParent(student.getParent());
        dto.setPreviousSchool(student.getPreviousSchool());
        dto.setPreviousPercentage(student.getPreviousPercentage());
        dto.setActive(student.isActive());

        return dto;
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