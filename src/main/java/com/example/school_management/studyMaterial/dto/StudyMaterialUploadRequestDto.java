package com.example.school_management.studyMaterial.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudyMaterialUploadRequestDto {

    @NotNull(message = "Class is required")
    private Long classSubjectTeacherEntityId;

    private List<String> urls;
}
