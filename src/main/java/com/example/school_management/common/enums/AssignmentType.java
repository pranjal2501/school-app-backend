package com.example.school_management.common.enums;

import lombok.Getter;

@Getter
public enum AssignmentType {
    PROJECT("Project"),
    QUIZ("Quiz"),
    HOMEWORK("Homework"),
    CLASSWORK("Classwork");

    private final String displayName;

    AssignmentType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}