package com.example.school_management.seeder;

import com.example.school_management.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/seeder")
@RequiredArgsConstructor
public class SeederController {

    private final DatabaseSeederService databaseSeederService;

    @PostMapping("/run")
    // Consider adding @PreAuthorize("hasRole('ADMIN')") if security is active
    public ResponseEntity<ApiResponse<String>> runSeeder() {
        databaseSeederService.executeSeeding();
        return ResponseEntity.ok(ApiResponse.ok("Database seeded successfully!", null));
    }
}