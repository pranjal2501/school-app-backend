package com.example.school_management.common.enums;


public enum Religion {

    HINDU("Hindu"),
    MUSLIM("Muslim"),
    CHRISTIAN("Christian"),
    SIKH("Sikh"),
    BUDDHIST("Buddhist"),
    JAIN("Jain"),
    PARSI("Parsi"),
    JEWISH("Jewish"),
    OTHER("Other");

    private final String displayName;

    Religion(String displayName) {
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
