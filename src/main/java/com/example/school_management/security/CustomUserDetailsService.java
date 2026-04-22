package com.example.school_management.security;

import com.example.school_management.admin.entity.Admin;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.admin.repository.AdminRepository;
import com.example.school_management.parent.repository.ParentRepository;
import com.example.school_management.teacher.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {

        var adminOpt = adminRepository.findByUsername(identifier);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            // Pass the ID here
            return buildUser(admin.getId(), admin.getUsername(), admin.getPassword(), "ADMIN");
        }

        var teacherOpt = teacherRepository.findByEmail(identifier)
                .or(() -> teacherRepository.findByMobile(identifier));
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            // Pass the ID here
            return buildUser(teacher.getId(), identifier, teacher.getPassword(), teacher.getRole().name());
        }

        var parentOpt = parentRepository.findByEmail(identifier)
                .or(() -> parentRepository.findByMobilePrimary(identifier));
        if (parentOpt.isPresent()) {
            Parent parent = parentOpt.get();
            // Pass the ID here
            return buildUser(parent.getId(), identifier, parent.getPassword(), "PARENT");
        }

        throw new UsernameNotFoundException("No user found: " + identifier);
    }

    private CustomUserDetails buildUser(Long id, String identifier, String password, String role) {
        return new CustomUserDetails(
                id,
                identifier,
                password,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}