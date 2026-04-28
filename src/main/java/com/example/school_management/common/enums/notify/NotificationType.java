package com.example.school_management.common.enums.notify;

public enum NotificationType {
    EVENT("Event"),
    EXAM("Exam"),
    RESULT("Result"),
    MEETING("Meeting"),
    INSPECTION("Inspection"),
    COMPLAINT("Complaint"),
    UPDATE("Update"),
    REMINDER("Reminder"),
    STUDENT_APPROVAL("Approve_Student");
    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}