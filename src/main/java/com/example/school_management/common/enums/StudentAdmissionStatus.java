package com.example.school_management.common.enums;

public enum StudentAdmissionStatus {
    PENDING_PARENT_APPROVAL,  // waiting for parent to approve
    ACTIVE,                   // approved and active
    REJECTED,                 // parent rejected
    EXPIRED                   // parent didn't respond within 48 hours
}
