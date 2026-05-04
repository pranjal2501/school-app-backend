package com.example.school_management.common.enums;

import lombok.Getter;

@Getter
public enum PlaceholderKey {

    // ── Student Basic Info ─────────────────────────────────────────────────────
    STUDENT_NAME("{{student.name}}", "Student Full Name"),
    STUDENT_FIRST_NAME("{{student.firstName}}", "Student First Name"),
    STUDENT_MIDDLE_NAME("{{student.middleName}}", "Student Middle Name"),
    STUDENT_LAST_NAME("{{student.lastName}}", "Student Last Name"),
    STUDENT_GENDER("{{student.gender}}", "Student Gender"),
    STUDENT_DOB("{{student.dob}}", "Student Date of Birth"),
    STUDENT_BLOOD_GROUP("{{student.bloodGroup}}", "Student Blood Group"),
    STUDENT_NATIONALITY("{{student.nationality}}", "Student Nationality"),
    STUDENT_RELIGION("{{student.religion}}", "Student Religion"),
    STUDENT_CATEGORY("{{student.category}}", "Student Category"),
    STUDENT_AADHAR("{{student.aadharNo}}", "Student Aadhar Number"),
    STUDENT_REG_ID("{{student.regId}}", "Student Registration ID"),

    // ── Student Academic Info ──────────────────────────────────────────────────
    STUDENT_ROLL_NUMBER("{{student.rollNumber}}", "Roll Number"),
    STUDENT_CLASS("{{student.class}}", "Class"),
    STUDENT_DIVISION("{{student.division}}", "Division"),
    STUDENT_STANDARD("{{student.standard}}", "Standard"),
    STUDENT_ACADEMIC_YEAR("{{student.academicYear}}", "Academic Year"),
    STUDENT_PREVIOUS_SCHOOL("{{student.previousSchool}}", "Previous School"),

    // ── Parent Info ────────────────────────────────────────────────────────────
    FATHER_NAME("{{parent.fatherName}}", "Father Full Name"),
    FATHER_OCCUPATION("{{parent.fatherOccupation}}", "Father Occupation"),
    MOTHER_NAME("{{parent.motherName}}", "Mother Full Name"),
    MOTHER_OCCUPATION("{{parent.motherOccupation}}", "Mother Occupation"),
    GUARDIAN_NAME("{{parent.guardianName}}", "Guardian Full Name"),
    GUARDIAN_RELATION("{{parent.guardianRelation}}", "Guardian Relation"),
    PARENT_ADDRESS("{{parent.address}}", "Parent Address"),
    PARENT_CITY("{{parent.city}}", "Parent City"),
    PARENT_STATE("{{parent.state}}", "Parent State"),
    PARENT_PINCODE("{{parent.pincode}}", "Parent Pincode"),
    PARENT_MOBILE("{{parent.mobile}}", "Parent Mobile"),
    PARENT_EMAIL("{{parent.email}}", "Parent Email"),

    // ── Bonafide Specific ──────────────────────────────────────────────────────
    BONAFIDE_PURPOSE("{{bonafide.purpose}}", "Purpose of Bonafide"),

    // ── LC Specific ────────────────────────────────────────────────────────────
    LC_LEAVING_DATE("{{lc.leavingDate}}", "Date of Leaving"),
    LC_REASON("{{lc.reason}}", "Reason for Leaving"),
    LC_CONDUCT("{{lc.conduct}}", "Student Conduct"),
    LC_LAST_ATTENDED_YEAR("{{lc.lastAttendedYear}}", "Last Attended Academic Year"),

    // ── General ────────────────────────────────────────────────────────────────
    CURRENT_DATE("{{date.today}}", "Today's Date"),
    CURRENT_YEAR("{{date.year}}", "Current Year");

    private final String token;
    private final String displayName;

    PlaceholderKey(String token, String displayName) {
        this.token = token;
        this.displayName = displayName;
    }
}