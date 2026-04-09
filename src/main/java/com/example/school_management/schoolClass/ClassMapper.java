package com.example.school_management.schoolClass;

import com.example.school_management.schoolClass.dto.ClassResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClassMapper {

    public ClassResponseDto toResponseDto(Class classEntity) {
        if (classEntity == null) {
            return null;
        }

        ClassResponseDto dto = new ClassResponseDto();
        dto.setId(classEntity.getId());
        dto.setStandard(classEntity.getStandard());
        dto.setDivision(classEntity.getDivision());
        dto.setTotalStudents(classEntity.getTotalStudents());
        dto.setBoys(classEntity.getBoys());
        dto.setGirls(classEntity.getGirls());
        dto.setClassName(classEntity.getClassName());

        // Safely extract teacher info to prevent infinite recursion
        if (classEntity.getClassTeacher() != null) {
            dto.setClassTeacherId(classEntity.getClassTeacher().getId());
            dto.setClassTeacherName(
                    classEntity.getClassTeacher().getFirstName() + " " +
                            classEntity.getClassTeacher().getLastName()
            );
        }

        return dto;
    }

    public List<ClassResponseDto> toResponseDtoList(List<Class> classEntities) {
        return classEntities.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}