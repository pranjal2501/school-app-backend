package com.example.school_management.academics.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sys_academic_structures", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"standard_name", "division_name"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "standard_name", nullable = false)
    private String standard; // e.g., "6", "Nursery"

    @Column(name = "division_name", nullable = false)
    private String division; // e.g., "A", "B", "Science"
}