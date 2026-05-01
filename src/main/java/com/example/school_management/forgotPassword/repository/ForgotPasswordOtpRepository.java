package com.example.school_management.forgotPassword.repository;

import com.example.school_management.forgotPassword.entity.ForgotPasswordOtp;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgotPasswordOtpRepository extends JpaRepository<ForgotPasswordOtp, Long> {
    ForgotPasswordOtp findByEmail(@NotNull String email);

    Optional<ForgotPasswordOtp> findTopByEmailOrderByCreatedAtDesc(String email);
}
