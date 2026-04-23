package com.example.school_management.studyMaterial.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudyMaterialUploadRequestDto {

    @NotEmpty(message = "At least one class allocation must be selected")
    private Set<Long> classSubjectTeacherIds;

    private String description;

    @NotNull(message = "Visibility flag is required")
    private Boolean isVisibleToParent;

    private List<String> urls;
}