package com.example.school_management.enums;

public enum Specialization {

    // Languages
    MARATHI("Marathi"),
    HINDI("Hindi"),
    ENGLISH("English"),
    SANSKRIT("Sanskrit"),
    URDU("Urdu"),

    // Mathematics
    MATHEMATICS("Mathematics"),
    ALGEBRA("Algebra"),
    GEOMETRY("Geometry"),

    // Sciences
    GENERAL_SCIENCE("General Science"),
    ENVIRONMENTAL_STUDIES("Environmental Studies"),
    EVS_PART1("EVS Part 1"),
    EVS_PART2("EVS Part 2"),
    SCIENCE_TECHNOLOGY_PART1("Science & Technology Part 1"),
    SCIENCE_TECHNOLOGY_PART2("Science & Technology Part 2"),

    // Social Sciences
    HISTORY_CIVICS("History & Civics"),
    HISTORY_POLITICAL_SCIENCE("History & Political Science"),
    GEOGRAPHY("Geography"),

    // Arts
    DRAWING("Drawing"),
    CALLIGRAPHY("Calligraphy"),

    // Physical & Health
    PHYSICAL_EDUCATION("Physical Education"),
    HEALTH_EDUCATION("Health Education"),
    YOGA("Yoga"),

    // Co-curricular
    MUSIC("Music"),
    DANCE("Dance"),
    DRAMA("Drama"),
    SPORTS("Sports"),

    // Technology
    COMPUTER_SCIENCE("Computer Science");

    private final String displayName;

    Specialization(String displayName) {
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