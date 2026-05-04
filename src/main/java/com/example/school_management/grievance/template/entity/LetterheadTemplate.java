package com.example.school_management.grievance.template.entity;

import com.example.school_management.common.enums.LetterheadType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "letterhead_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LetterheadTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50, unique = true)
    private LetterheadType type;

    @Column(name = "letterhead_url", nullable = false, length = 500)
    private String letterheadUrl;

    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "font_size")
    private Integer fontSize;

    @Column(name = "text_color", length = 10)
    private String textColor;               // hex e.g. "#000000"

    @Column(name = "text_x_position")
    private Integer textXPosition;

    @Column(name = "text_y_position")
    private Integer textYPosition;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}