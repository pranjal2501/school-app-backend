package com.example.school_management.forgotPassword.entity;

import com.example.school_management.common.BaseEntity;
import com.example.school_management.common.ValidationMessages;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "forgot_password_otps")
public class ForgotPasswordOtp extends BaseEntity {

    @Email(message = ValidationMessages.EMAIL_INVALID)
    @Size(max = 100)
    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Size(min = 6, max = 6)
    @Column(name = "otp", length = 6, nullable = false)
    private String otp;

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

    @Builder.Default
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @PrePersist
    public void prePersist() {
        if (this.expireAt == null) {
            this.expireAt = LocalDateTime.now().plusMinutes(10);
        }
    }
}