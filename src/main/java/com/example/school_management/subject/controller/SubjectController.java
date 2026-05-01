package com.example.school_management.subject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
@Validated // Required when validating a List<> of DTOs in the request body
public class SubjectController {

}