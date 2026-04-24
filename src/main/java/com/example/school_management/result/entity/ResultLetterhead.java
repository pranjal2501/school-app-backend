package com.example.school_management.result.entity;

import com.example.school_management.common.BaseEntity;
import com.example.school_management.common.enums.ExamTerm;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "result_letterheads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultLetterhead extends BaseEntity {

    @Column(nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamTerm examTerm;

    @Column(nullable = false, length = 10)
    private String academicYear;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isResultPublished = false;

    @Column
    private LocalDateTime publishedAt;
}
