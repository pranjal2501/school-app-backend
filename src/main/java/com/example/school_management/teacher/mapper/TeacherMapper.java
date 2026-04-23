package com.example.school_management.teacher.mapper;

import com.example.school_management.subject.entity.Subject;
import com.example.school_management.teacher.dto.TeacherFromAdminRequestDto;
import com.example.school_management.teacher.dto.TeacherRemainingInfoDto;
import com.example.school_management.teacher.dto.response.TeacherResponseDto;
import com.example.school_management.teacher.entity.Teacher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeacherMapper {

    /**
     * Maps incoming DTO to Teacher Entity using SuperBuilder.
     */
    public Teacher toEntity(TeacherFromAdminRequestDto request) {
        if (request == null) {
            return null;
        }

        return Teacher.builder()
                // Employee Base Fields
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .gender(request.getGender())
                .dob(request.getDob())
                // Teacher Specific Fields
                .joiningDate(request.getJoiningDate())
                .yearsOfExperienceInCurrentSchool(request.getYearsOfExperienceInCurrentSchool())
                .aadharNo(request.getAadharNo())
                .panNo(request.getPanNo())
                .build();
    }

    /**
     * Maps Teacher Entity to Response DTO using Builder.
     * Ensure TeacherResponseDto has the @Builder annotation!
     */
    public TeacherResponseDto toResponseDto(Teacher teacher) {
        if (teacher == null) {
            return null;
        }

        // Map Many-to-Many Subjects to a simple List of Strings before building
        List<String> mappedSubjects = null;
        if (teacher.getSubjects() != null && !teacher.getSubjects().isEmpty()) {
            mappedSubjects = teacher.getSubjects().stream()
                    .map(Subject::getSubjectName)
                    .toList();
        }

        return TeacherResponseDto.builder()
                // Base / System Fields
                .id(teacher.getId())
                .teacherId(teacher.getTeacherId())
                .createdAt(teacher.getCreatedAt())
                .updatedAt(teacher.getUpdatedAt())
                .active(teacher.isActive())
                .role(teacher.getRole())
                // Personal Info
                .firstName(teacher.getFirstName())
                .middleName(teacher.getMiddleName())
                .lastName(teacher.getLastName())
                .email(teacher.getEmail())
                .mobile(teacher.getMobile())
                .gender(teacher.getGender())
                .dob(teacher.getDob())
                .joiningDate(teacher.getJoiningDate())
                .aadharNo(teacher.getAadharNo())
                .panNo(teacher.getPanNo())
                // Address Fields
                .addressLine1(teacher.getAddressLine1())
                .addressLine2(teacher.getAddressLine2())
                .city(teacher.getCity())
                .state(teacher.getState())
                .pincode(teacher.getPincode())
                // Education & Experience
                .degreeType(teacher.getDegreeType())
                .customDegreeName(teacher.getCustomDegreeName())
                .yearsOfExperienceInCurrentSchool(teacher.getYearsOfExperienceInCurrentSchool())
                .previousSchool(teacher.getPreviousSchool())
                .yearsOfExperienceInPreviousSchool(teacher.getYearsOfExperienceInPreviousSchool())
                // URLs
                .profilePicUrl(teacher.getProfilePicUrl())
                .aadharPhotourl(teacher.getAadharPhotourl())
                .panPhotourl(teacher.getPanPhotourl())
                .degreeCertificateurl(teacher.getDegreeCertificateurl())
                .resignationLetterurl(teacher.getResignationLetterurl())
                .resumeurl(teacher.getResumeurl())
                // Collections
                .subjects(mappedSubjects)
                .specializations(teacher.getSpecializations())
                .build();
    }

    /**
     * Mutates an existing Hibernate-managed entity.
     * Do NOT use a Builder here, otherwise Hibernate loses track of the entity state.
     */
    public void updateTeacherFromRemainingInfoDto(TeacherRemainingInfoDto dto, Teacher teacher) {
        if (dto == null || teacher == null) {
            return;
        }

        // Address Fields
        if (dto.getAddressLine1() != null) teacher.setAddressLine1(dto.getAddressLine1());
        if (dto.getAddressLine2() != null) teacher.setAddressLine2(dto.getAddressLine2());
        if (dto.getCity() != null) teacher.setCity(dto.getCity());
        if (dto.getState() != null) teacher.setState(dto.getState());
        if (dto.getPincode() != null) teacher.setPincode(dto.getPincode());

        // Education Fields
        if (dto.getDegreeType() != null) teacher.setDegreeType(dto.getDegreeType());
        if (dto.getCustomDegreeName() != null) teacher.setCustomDegreeName(dto.getCustomDegreeName());

        // Document URLs
        if(dto.getProfilePicUrl() != null) teacher.setProfilePicUrl(dto.getProfilePicUrl());
        if (dto.getAadharPhotourl() != null) teacher.setAadharPhotourl(dto.getAadharPhotourl());
        if (dto.getPanPhotourl() != null) teacher.setPanPhotourl(dto.getPanPhotourl());
        if (dto.getDegreeCertificateurl() != null) teacher.setDegreeCertificateurl(dto.getDegreeCertificateurl());
        if (dto.getResignationLetterurl() != null) teacher.setResignationLetterurl(dto.getResignationLetterurl());
        if (dto.getResumeurl() != null) teacher.setResumeurl(dto.getResumeurl());

        // Experience
        if (dto.getPreviousSchool() != null) teacher.setPreviousSchool(dto.getPreviousSchool());
        if (dto.getYearsOfExperienceInPreviousSchool() != null) teacher.setYearsOfExperienceInPreviousSchool(dto.getYearsOfExperienceInPreviousSchool());

        // Specializations
        if (dto.getSpecializations() != null) {
            teacher.setSpecializations(dto.getSpecializations());
        }
    }
}