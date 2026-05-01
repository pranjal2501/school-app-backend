package com.example.school_management.common.enums.notify;

import lombok.Getter;

@Getter
public enum AudienceType {
    PARENT("Parent"),
    TEACHER("Teacher"),
    ADMIN("Admin");
    private final String displayName;

    AudienceType(String displayName) {
        this.displayName = displayName;
    }

}