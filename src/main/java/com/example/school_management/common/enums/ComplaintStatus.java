package com.example.school_management.common.enums;

import com.example.school_management.complaint.entity.Complaint;

public enum ComplaintStatus {
    PENDING,
    RESOLVED,
    UNRESOLVED;

    public void validateTransitionTo(ComplaintStatus nextStatus, Complaint complaint) {


        if (this == nextStatus) {
            throw new RuntimeException("Complaint is already marked as " + this.name());
        }

        boolean isValid = false;
        String allowedNextSteps = switch (this) {
            case PENDING -> {
                isValid = (nextStatus == UNRESOLVED || nextStatus == RESOLVED);
                yield "UNRESOLVED, RESOLVED";
            }
            case RESOLVED, UNRESOLVED -> {
                yield "None (This is a terminal state)";
            }
        };

        if (!isValid) {
            throw new RuntimeException(
                    String.format(
                            "Invalid status transition. Cannot jump from %s to %s. Allowed next steps: [%s]",
                            this.name(), nextStatus.name(), allowedNextSteps
                    )
            );
        }

        if (nextStatus == RESOLVED) {
            if (complaint.getResolutionComment() == null
                    || complaint.getResolutionComment().trim().isEmpty()) {
                throw new RuntimeException(
                        "Resolution comment is mandatory to mark complaint as RESOLVED."
                );
            }
            if (complaint.getResolvedBy() == null) {
                throw new RuntimeException(
                        "ResolvedBy field is mandatory to mark complaint as RESOLVED."
                );
            }
        }
    }
}