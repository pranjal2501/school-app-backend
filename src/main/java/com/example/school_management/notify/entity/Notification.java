package com.example.school_management.notify.entity;

import com.example.school_management.common.enums.notify.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;         // full details — shown to teachers only

    @Column(nullable = false)
    private String shortInfo;          // minimal info — shown to parents

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AudienceType audienceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50 )
    private NotificationType notificationType;

    @Column(nullable = false)
    private LocalDateTime eventDateTime;        // actual date of event/exam/result

    @Column(nullable = false)
    private LocalDateTime scheduledDispatchTime; // computed: eventDate - 3 or 10 days

    @Column(nullable = false)
    @Builder.Default
    private Boolean dispatched = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (Boolean.TRUE.equals(this.dispatched)) return;

        // Dispatch window: 3 days before for parents, 10 days for teachers
        if (this.audienceType == AudienceType.PARENT) {
            this.scheduledDispatchTime = this.eventDateTime.minusDays(3);
        } else {
            this.scheduledDispatchTime = this.eventDateTime.minusDays(10);
        }
    }

//    Testing Purpose
//    @PrePersist
//    public void prePersist() {
//        this.createdAt = LocalDateTime.now();
//          if (Boolean.TRUE.equals(this.dispatched)) return;
//        // TEMP: dispatch immediately for testing
//        this.scheduledDispatchTime = LocalDateTime.now().minusMinutes(1);
//    }
}