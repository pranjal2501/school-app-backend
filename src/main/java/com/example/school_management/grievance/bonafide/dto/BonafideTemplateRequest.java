package com.example.school_management.grievance.bonafide.dto;
//Admin creates/updates the template

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonafideTemplateRequest {

    @NotBlank(message = "Letterhead URL is required")
    @Size(max = 500)
    private String letterheadUrl;

    @NotBlank(message = "Content is required")
    @Size(max = 2000, message = "Content must be under 2000 characters")
    private String content;

    private Integer fontSize;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Color must be a valid hex e.g. #000000")
    private String textColor;

    private Integer textXPosition;
    private Integer textYPosition;
}
