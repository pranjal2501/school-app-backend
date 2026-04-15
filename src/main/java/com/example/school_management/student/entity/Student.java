package com.example.school_management.student.entity;

import com.example.school_management.admin.entity.Admin;
import com.example.school_management.attendance.entity.LeaveRequests;
import com.example.school_management.attendance.entity.StudentAttendance;
import com.example.school_management.common.enums.*;
import com.example.school_management.complaint.entity.Complaint;
import com.example.school_management.common.ValidationMessages;
import com.example.school_management.common.BaseEntity;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "students", uniqueConstraints = @UniqueConstraint(columnNames = {"class_id", "roll_number"}))
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class Student extends BaseEntity {

    @Column(name = "registration_Id", unique = true, nullable = false, updatable = false)
    private String regId;

    @PrePersist
    private void generateRegId() {
        this.regId = "REG-STUD" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    @NotBlank(message = ValidationMessages.FIRST_NAME_REQUIRED)
    @Size(max = 50, message = ValidationMessages.FIRST_NAME_MAX)
    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    @Size(max = 50, message = ValidationMessages.MIDDLE_NAME_MAX)
    @Column(name = "middle_name", length = 50)
    private String middleName;

    @NotBlank(message = ValidationMessages.LAST_NAME_REQUIRED)
    @Size(max = 50, message = ValidationMessages.LAST_NAME_MAX)
    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10, nullable = false, updatable = false)
    private Gender gender;

    @NotNull(message = ValidationMessages.DOB_REQUIRED)
    @Past(message = ValidationMessages.DOB_PAST)
    @Column(name = "dob", nullable = false, updatable = false)
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group", length = 15, nullable = true, updatable = false)
    private BloodGroup bloodGroup;

    @NotBlank(message = ValidationMessages.AADHAR_REQUIRED)
    @Pattern(regexp = "^[0-9]{12}$", message = ValidationMessages.AADHAR_PATTERN)
    @Column(name = "aadhar_no", length = 12, nullable = false, unique = true, updatable = false)
    private String aadharNo;

    @Size(max = 50, message = ValidationMessages.NATIONALITY_MAX)
    @Column(name = "nationality", length = 50, nullable = true)
    private String nationality;

    @Enumerated(EnumType.STRING)
    @Column(name = "religion", length = 30, nullable = true)
    private Religion religion;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 30, nullable = true)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private SchoolClass assignedClass;

    @NotNull
    private String standard;

    @Min(value = 1, message = ValidationMessages.ROLL_NUMBER_MIN)
    @Column(name = "roll_number", unique = false, nullable = true)
    private Integer rollNumber;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @Size(max = 150, message = ValidationMessages.PREVIOUS_SCHOOL_MAX)
    @Column(name = "previous_school", length = 150)
    private String previousSchool;

    @DecimalMin(value = "0.0", message = ValidationMessages.PERCENTAGE_MIN)
    @DecimalMax(value = "100.0", message = ValidationMessages.PERCENTAGE_MAX)
    @Column(name = "previous_percentage")
    private Double previousPercentage;

    @Size(max = 200)
    @Column(name = "birth_certificate_url", length = 200, updatable = false)
    private String birthCertificateUrl;

    @Size(max = 200)
    @Column(name = "aadhar_photo_url", length = 200, updatable = false)
    private String aadharPhotoUrl;

    @Size(max = 200)
    @Column(name = "previous_marksheet_url", length = 200)
    private String previousMarksheetUrl;

    @Size(max = 200)
    @Column(name = "passport_photo_url", length = 200, updatable = false)
    private String passportPhotoUrl;

    @Size(max = 200)
    @Column(name = "lc_url", length = 200)
    private String leavingCertificateUrl;

    @Size(max = 200)
    @Column(name = "caste_certificate_url", length = 200)
    private String casteCertificateUrl;

    @Size(max = 200)
    @Column(name = "income_certificate_url", length = 200)
    private String incomeCertificateUrl;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_admin")
    private Admin addedBy;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "targetStudent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Complaint> complaints = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<LeaveRequests> leaveRequests;
}