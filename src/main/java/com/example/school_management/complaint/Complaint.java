package com.example.school_management.complaint;

import com.example.school_management.admin.Admin;
import com.example.school_management.common.BaseEntity;
import com.example.school_management.employee.Employee;
import com.example.school_management.student.Student;
import com.example.school_management.teacher.Teacher;
import com.example.school_management.common.enums.ComplaintCategory;
import com.example.school_management.common.enums.ComplaintStatus;
import com.example.school_management.common.enums.Priority;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "complaints")
@Getter
@Setter
/*
If you call .toString() or .hashCode() on a Complaint, it will traverse the Student, Teacher, and
supportedMedia collections. Since those are FetchType.LAZY, doing this outside an active transaction will
instantly throw a LazyInitializationException.
 */
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Complaint extends BaseEntity {

    // 1. User-Facing Tracking ID (e.g., CMP-4F9A...)
    @Column(name = "ticket_number", nullable = false, unique = true, updatable = false, length = 20)
    private String ticketNumber;

    // 2. Concurrency Control (Optimistic Locking)
    @Version
    // Hibernate will automatically track the version of the row. If two admins try to update it
    // simultaneously, the second one will get an OptimisticLockException, preventing silent data overrides.
    @Column(name = "version")
    private Long version;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private ComplaintCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ComplaintStatus status = ComplaintStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 10)
    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    /*
    The items in this list (String) don't have their own lifecycle. They belong entirely to this Complaint.
    If the Complaint is deleted, delete these strings too. By default, Element Collections are fetched lazily.
     */
    @ElementCollection
    @CollectionTable(name = "complaint_media", joinColumns = @JoinColumn(name = "complaint_id"))
    /*
    length = 500 is crucial here. The default JPA string length is 255 characters. URLs
    (especially those from AWS S3, Firebase, or Cloudinary with security tokens attached) frequently
    exceed 255 characters. This prevents database truncation errors.
     */
    @Column(name = "media_url", length = 500)
    /*
     @ElementCollection is loaded lazily, hence excluding toString() prevents LazyInitializationException.
     */
    @ToString.Exclude
    @Builder.Default
    private List<String> supportedMedia = new ArrayList<>();

    @Column(name = "resolution_comment", length = 1000)
    private String resolutionComment;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_employee_id")
    @ToString.Exclude
    private Employee resolvedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_student_id")
    @ToString.Exclude
    private Student targetStudent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raised_by_employee")
    @ToString.Exclude
    private Employee reportedByEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_admin_id")
    @ToString.Exclude
    private Admin resolvedByAdmin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by_admin_id")
    @ToString.Exclude
    private Admin reportedByAdmin;

    @PrePersist
    protected void onCreate() {
        // Auto-generate a secure, readable ticket number before hitting the DB
        if (this.ticketNumber == null) {
            this.ticketNumber = "CMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        // Automatically timestamp resolutions to prevent developer oversight
        if (this.status == ComplaintStatus.RESOLVED && this.resolvedAt == null) {
            this.resolvedAt = Instant.now();
        }

        // Ensure resolution comments/resolvers exist if marked resolved
        if (this.status == ComplaintStatus.RESOLVED && this.resolvedBy == null) {
            throw new IllegalStateException("A complaint must have a assigned 'resolvedBy' employee before marking as RESOLVED.");
        }
    }
}