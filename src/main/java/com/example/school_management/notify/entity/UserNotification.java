package com.example.school_management.notify.entity;

import com.example.school_management.admin.entity.Admin;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    // replace userId+role with direct FK to actual entities
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    private LocalDateTime readAt;

    @Column(nullable = false)
    private LocalDateTime deliveredAt;


    @Column(nullable = false)
    @Builder.Default
    private Boolean feePaid = false;

    @Column
    private LocalDateTime feePaidAt;

    @Column
    private Double feeAmount;  // copied from notification at delivery time

    @PrePersist
    public void prePersist() {

        if(this.notification.getRequiresFee()==false)
        {
            this.feeAmount=null;
            this.feePaidAt=null;
        }
    }
}