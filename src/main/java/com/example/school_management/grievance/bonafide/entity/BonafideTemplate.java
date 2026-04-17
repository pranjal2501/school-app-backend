package com.example.school_management.grievance.bonafide.entity;

import com.example.school_management.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bonafide_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonafideTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private Integer textXPosition;          // pixel position on letterhead

    @Column(name = "text_y_position")
    private Integer textYPosition;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}