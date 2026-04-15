package com.example.school_management.common.enums;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/v1/enums")
@CrossOrigin(origins = "*")
public class EnumController {

    @GetMapping("/subject-types")
    public ResponseEntity<List<Map<String, String>>> getSubjectTypes() {
        List<Map<String, String>> list = new ArrayList<>();
        for (SubjectType type : SubjectType.values()) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("value", type.name());
            map.put("displayName", type.name());
            list.add(map);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/religions")
    public ResponseEntity<List<Map<String, String>>> getReligions() {
        List<Map<String, String>> list = new ArrayList<>();
        for (Religion religion : Religion.values()) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("value", religion.name());
            map.put("displayName", religion.getDisplayName());
            list.add(map);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/genders")
    public ResponseEntity<List<Map<String, String>>> getGenders() {
        List<Map<String, String>> list = new ArrayList<>();
        for (Gender gender : Gender.values()) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("value", gender.name());
            map.put("displayName", gender.name());
            list.add(map);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/degree-types")
    public ResponseEntity<List<Map<String, String>>> getDegreeTypes() {
        List<Map<String, String>> list = new ArrayList<>();
        for (DegreeType degreeType : DegreeType.values()) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("value", degreeType.name());
            map.put("displayName", degreeType.getDisplayName());
            list.add(map);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, String>>> getCategories() {
        List<Map<String, String>> list = new ArrayList<>();
        for (Category category : Category.values()) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("value", category.name());
            map.put("displayName", category.getDisplayName());
            list.add(map);
        }
        return ResponseEntity.ok(list);
    }





    @GetMapping("/blood-groups")
    public ResponseEntity<List<Map<String, String>>> getBloodGroups() {
        List<Map<String, String>> list = new ArrayList<>();
        for (BloodGroup bloodGroup : BloodGroup.values()) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("value", bloodGroup.name());
            map.put("displayName", bloodGroup.getDisplayName());
            list.add(map);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/specialization")
    public ResponseEntity<List<Map<String, String>>> getSpecialization() {
        List<Map<String, String>> list = new ArrayList<>();
        for (Specialization specialization : Specialization.values()) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("value", specialization.name());
            map.put("displayName", specialization.getDisplayName());
            list.add(map);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/address-proof")
    public ResponseEntity<List<Map<String, String>>> getAddressProof() {
        List<Map<String, String>> list = new ArrayList<>();
        for (AddressProofDocument ap : AddressProofDocument.values()) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("value", ap.name());
            map.put("displayName", ap.getDisplayName());
            list.add(map);
        }
        return ResponseEntity.ok(list);
    }
}