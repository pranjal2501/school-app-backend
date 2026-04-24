package com.example.school_management.result.dto.resultDisplay;


import com.example.school_management.common.enums.ExamTerm;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class ToggleResultStatus {

    private ExamTerm examTerm;

    private String academicYear;

    private Boolean publish;
}
