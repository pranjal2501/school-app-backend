package com.example.school_management.notify.entity;

import com.example.school_management.common.enums.FeeType;
import com.example.school_management.common.enums.notify.*;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "notifications")
@Getter
@Setter
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

    @Column(nullable = true, name = "supporting_urls")
    @Builder.Default
    private List<String> urls = null;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "notification_target_classes",
            joinColumns = @JoinColumn(name = "notification_id"),
            inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    private Set<SchoolClass> targetClasses = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "notification_teacher_targets",
            joinColumns = @JoinColumn(name = "notification_id"))
    @Column(name = "teacher_id")
    private Set<Long> teacherIds = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_teacher_id")
    private Teacher createdByTeacher;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        if (this.scheduledDispatchTime != null) return; // already set by mapper (dispatchNow = true)

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