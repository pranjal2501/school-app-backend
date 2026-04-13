package com.example.school_management.teacher.mapper;

import com.example.school_management.subject.entity.Subject;
import com.example.school_management.teacher.dto.TeacherFromAdminRequestDto;
import com.example.school_management.teacher.dto.TeacherRemainingInfoDto;
import com.example.school_management.teacher.dto.TeacherResponseDto;
import com.example.school_management.teacher.entity.Teacher;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TeacherMapper {

    /**
     * Maps incoming DTO to Teacher Entity.
     * Only handles raw data transfer; business logic remains in the service.
     */
    public Teacher toEntity(TeacherFromAdminRequestDto request) {
        if (request == null) {
            return null;
        }

        Teacher teacher = new Teacher();

        // Employee Base Fields
        teacher.setFirstName(request.getFirstName());
        teacher.setMiddleName(request.getMiddleName());
        teacher.setLastName(request.getLastName());
        teacher.setEmail(request.getEmail());
        teacher.setMobile(request.getMobile());
        teacher.setGender(request.getGender());
        teacher.setDob(request.getDob());

        // Teacher Specific Fields
        teacher.setJoiningDate(request.getJoiningDate());
        teacher.setYearsOfExperienceInCurrentSchool(request.getYearsOfExperienceInCurrentSchool());
        teacher.setAadharNo(request.getAadharNo());
        teacher.setPanNo(request.getPanNo());

        return teacher;
    }

    public TeacherResponseDto toResponseDto(Teacher teacher) {
        if (teacher == null) {
            return null;
        }

        TeacherResponseDto response = new TeacherResponseDto();

        // Base / Onboarding Fields
        response.setId(teacher.getId());
        response.setCreatedAt(teacher.getCreatedAt());
        response.setUpdatedAt(teacher.getUpdatedAt());
        response.setFirstName(teacher.getFirstName());
        response.setMiddleName(teacher.getMiddleName());
        response.setLastName(teacher.getLastName());
        response.setEmail(teacher.getEmail());
        response.setMobile(teacher.getMobile());
        response.setGender(teacher.getGender());
        response.setDob(teacher.getDob());
        response.setJoiningDate(teacher.getJoiningDate());
        response.setAadharNo(teacher.getAadharNo());
        response.setPanNo(teacher.getPanNo());
        response.setActive(teacher.isActive());
        response.setRole(teacher.getRole());

        // Address Fields
        response.setAddressLine1(teacher.getAddressLine1());
        response.setAddressLine2(teacher.getAddressLine2());
        response.setCity(teacher.getCity());
        response.setState(teacher.getState());
        response.setPincode(teacher.getPincode());

        // Education & Experience
        response.setDegreeType(teacher.getDegreeType());
        response.setCustomDegreeName(teacher.getCustomDegreeName());
        response.setYearsOfExperienceInCurrentSchool(teacher.getYearsOfExperienceInCurrentSchool());
        response.setPreviousSchool(teacher.getPreviousSchool());
        response.setYearsOfExperienceInPreviousSchool(teacher.getYearsOfExperienceInPreviousSchool());

        // URLs
        response.setAadharPhotourl(teacher.getAadharPhotourl());
        response.setPanPhotourl(teacher.getPanPhotourl());
        response.setDegreeCertificateurl(teacher.getDegreeCertificateurl());
        response.setResignationLetterurl(teacher.getResignationLetterurl());
        response.setResumeurl(teacher.getResumeurl());

        // Map Many-to-Many Subjects to a simple List of Strings for the DTO
        if (teacher.getSubjects() != null && !teacher.getSubjects().isEmpty()) {
            response.setSubjects(teacher.getSubjects().stream()
                    .map(Subject::getSubjectName)
                    .collect(Collectors.toList()));
        }

        // Map Specializations
        response.setSpecializations(teacher.getSpecializations());

        return response;
    }

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
        if (dto.getAadharPhotourl() != null) teacher.setAadharPhotourl(dto.getAadharPhotourl());
        if (dto.getPanPhotourl() != null) teacher.setPanPhotourl(dto.getPanPhotourl());
        if (dto.getDegreeCertificateurl() != null) teacher.setDegreeCertificateurl(dto.getDegreeCertificateurl());
        if (dto.getResignationLetterurl() != null) teacher.setResignationLetterurl(dto.getResignationLetterurl());
        if (dto.getResumeurl() != null) teacher.setResumeurl(dto.getResumeurl());

        // Experience
        if (dto.getPreviousSchool() != null)
            teacher.setPreviousSchool(dto.getPreviousSchool());

        if (dto.getYearsOfExperienceInPreviousSchool() != null)
            teacher.setYearsOfExperienceInPreviousSchool(dto.getYearsOfExperienceInPreviousSchool());

        // Specializations (Enums can be mapped directly)
        if (dto.getSpecializations() != null) {
            teacher.setSpecializations(dto.getSpecializations());
        }
    }
}