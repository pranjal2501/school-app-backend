package com.example.school_management.seeder;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.seeder.DatabaseSeederService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/seeder")
@RequiredArgsConstructor
public class DataSeederController {

    private final DatabaseSeederService databaseSeederService;

    // Call this endpoint ONCE on a fresh database.
    @PostMapping("/run")
    public ResponseEntity<ApiResponse<Void>> runSeeder() {
        databaseSeederService.seedDatabase();
        return ResponseEntity.ok(ApiResponse.ok("Database seeded successfully.", null));
    }
}