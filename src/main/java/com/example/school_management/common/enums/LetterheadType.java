package com.example.school_management.common.enums;

public enum LetterheadType {

    // Fits LetterheadTemplate
    BONAFIDE_CERTIFICATE("Bonafide Certificate"),
    CHARACTER_CERTIFICATE("Character Certificate"),
    STUDY_CERTIFICATE("Study Certificate"),
    DATE_OF_BIRTH_CERTIFICATE("Date of Birth Certificate"),
    ADDRESS_PROOF_LETTER("Address Proof Letter"),
    NO_DUES_CERTIFICATE("No Dues Certificate"),
    TRANSPORT_CERTIFICATE("Transport Certificate"),  // Leaving Certificate
    HOSTEL_CERTIFICATE("Hostel Certificate"),
    CASTE_CATEGORY_SUPPORT_LETTER("Caste/Category Support Letter"),
    MEDICAL_CERTIFICATE("Medical Certificate"),
    FITNESS_CERTIFICATE("Fitness Certificate"),
    SPORTS_CERTIFICATE("Sports Certificate"),
    ACHIEVEMENT_CERTIFICATE("Achievement Certificate"),
    PARTICIPATION_CERTIFICATE("Participation Certificate"),
    NSS_NCC_CERTIFICATE("NSS/NCC Certificate"),
    MIGRATION_CERTIFICATE("Migration Certificate"),
    LEAVING_CERTIFICATE("Leaving Certificate"),
    TRANSFER_CERTIFICATE("Transfer Certificate"),
    FEE_STRUCTURE_LETTER("Fee Structure Letter"),
    FEE_RECEIPT("Fee Receipt"),
    SCHOLARSHIP_CERTIFICATE("Scholarship Certificate"),

    // Does NOT fit LetterheadTemplate
    MARK_SHEET_COPY("Mark Sheet Copy"),
    GRADE_CERTIFICATE("Grade Certificate"),
    PROGRESS_REPORT("Progress Report"),
    DUPLICATE_ID_CARD("Duplicate ID Card"),
    SIBLING_DISCOUNT_REQUEST("Sibling Discount Request"),
    NAME_CORRECTION_REQUEST("Name Correction Request");

    private final String displayName;

    LetterheadType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}