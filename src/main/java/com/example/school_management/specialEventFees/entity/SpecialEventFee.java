package com.example.school_management.specialEventFees.entity;

import com.example.school_management.common.BaseEntity;
import com.example.school_management.common.enums.FeeType;
import com.example.school_management.notify.entity.Notification;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "special_event_fees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialEventFee extends BaseEntity {

    @Column(nullable = false)
    private String eventName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double feeAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeType feeType;

    @Column(nullable = false)
    private LocalDateTime paymentDeadline;

    @Column(nullable = false)
    private LocalDateTime eventDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_teacher_id", nullable = false)
    private Teacher createdByTeacher;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id")
    private Notification notification;
}
