package com.example.school_management.common.enums;

public enum AddressProofDocument {

    // Government IDs
    AADHAAR_CARD("Aadhaar Card"),
    PASSPORT("Passport"),
    VOTER_ID_CARD("Voter ID Card"),
    DRIVING_LICENSE("Driving License"),
    DOMICILE_CERTIFICATE("Domicile Certificate"),

    // Utility Bills (Usually required to be within the last 3 months)
    ELECTRICITY_BILL("Electricity Bill"),
    WATER_BILL("Water Bill"),
    GAS_CONNECTION_BILL("Gas Connection Bill"),
    TELEPHONE_BROADBAND_BILL("Telephone/Broadband Bill"),

    // Financial & Property
    BANK_PASSBOOK_STATEMENT("Bank Passbook/Statement"),
    POST_OFFICE_ACCOUNT_STATEMENT("Post Office Account Statement"),
    RENT_AGREEMENT("Rent/Lease Agreement"),
    PROPERTY_TAX_RECEIPT("Property Tax Receipt");

    private final String displayName;

    AddressProofDocument(String displayName) {
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