package com.example.school_management.auth;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.security.CustomUserDetails;
import com.example.school_management.security.CustomUserDetailsService;
import com.example.school_management.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomUserDetailsService service;
    @Autowired
    private PasswordEncoder encoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        UserDetails userDetails;
        try {
            userDetails = service.loadUserByUsername(request.getUsername());
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid credentials"));
        }

        if (!encoder.matches(request.getPassword(), userDetails.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid credentials"));
        }

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("UNKNOWN");

        role = role.replace("ROLE_", "");
        String token = jwtUtil.generateToken(userDetails.getUsername(), role);

        // Extract the ID from our CustomUserDetails
        Long userId = null;
        if (userDetails instanceof CustomUserDetails customUser) {
            userId = customUser.getId();
        }

        // Build the response map safely
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("id", userId);
        responseData.put("token", token);
        responseData.put("role", role);
        responseData.put("identifier", userDetails.getUsername());

        return ResponseEntity.ok(
                ApiResponse.ok("Login successful", responseData)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(ApiResponse.ok("Logout successful", null));
    }
}