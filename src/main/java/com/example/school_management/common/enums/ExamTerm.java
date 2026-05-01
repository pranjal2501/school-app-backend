package com.example.school_management.common.enums;

public enum ExamTerm {
    UNIT_TEST_1("Unit Test 1"),
    UNIT_TEST_2("Unit Test 2"),
    MID_TERM("Mid Terms"),
    PRELIMS("Prelims"),
    FINAL_EXAM("Final Examinations");

    private final String displayName;

    ExamTerm(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}