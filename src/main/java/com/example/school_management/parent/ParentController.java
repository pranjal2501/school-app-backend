package com.example.school_management.parent;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ParentController {

//    Dashboard
//    GET /api/v1/parents/dashboard/{studentId} — attendance %, avg score, pending assignments, fees due, recent activity
//
//    Child Details
//    GET /api/v1/parents/children — list all children with class, roll, performance tag (Good/Excellent)
//    GET /api/v1/parents/children/{studentId} — child detail: name, class, roll, attendance %, avg score, rank, class teacher, academic year
//
//    Attendance
//    GET /api/v1/parents/children/{studentId}/attendance?month=3&year=2026 — monthly attendance with present, absent, holiday count and daily calendar data
//
//    Assignments
//    GET /api/v1/parents/children/{studentId}/assignments — all assignments with status (pending/submitted), subject, due date
//    GET /api/v1/parents/children/{studentId}/assignments?status=PENDING — filtered by status
//
//    Results
//    GET /api/v1/parents/children/{studentId}/results?examType=MID_TERM — results per exam type with subject wise marks, grade, avg, highest, lowest
//
//    Notifications
//    GET /api/v1/parents/children/{studentId}/notifications — child specific (attendance alert, assignment pending, result published)
//    GET /api/v1/parents/notifications — parent level only (PTM, general school announcements)
//
//    Profile
//    GET /api/v1/parents/profile — parent profile details

}
