package com.example.school_management.common;

import com.example.school_management.common.enums.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleVerifier {

    public Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Authentication required to perform this action");
        }
        return authentication;
    }

    /**
     * Checks if the currently logged-in user has the specific exact Role.
     */
    public boolean hasRole(Role role) {
        Authentication authentication = getAuthentication();
        String targetRole = "ROLE_" + role.name();

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(targetRole));
    }

    /**
     * Checks if the currently logged-in user possesses AT LEAST ONE of the provided roles.
     */
    public boolean hasAnyRole(Role... roles) {
        Authentication authentication = getAuthentication();

        Set<String> targetRoles = Arrays.stream(roles)
                .map(r -> "ROLE_" + r.name())
                .collect(Collectors.toSet());

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(targetRoles::contains);
    }

    public String getCurrentUsername() {
        return getAuthentication().getName();
    }
}