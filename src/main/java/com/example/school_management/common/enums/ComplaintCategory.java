package com.example.school_management.common.enums;

public enum ComplaintCategory {
    ACADEMIC("Academic"),
    DISCIPLINE("Discipline"),
    HARASSMENT("Harassment"),
    INFRASTRUCTURE("Infrastructure"),     // e.g., broken desks, fans
    HOSTEL_OR_MESS("Hostel/Mess"),
    TRANSPORT("Transport"),          // e.g., bus issues
    ADMINISTRATIVE("Administrative"),     // e.g., fee payment issues
    OTHER("Other");

    private final String displayName;

    ComplaintCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}