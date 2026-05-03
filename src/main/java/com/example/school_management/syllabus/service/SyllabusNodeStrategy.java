package com.example.school_management.syllabus.service;

public interface SyllabusNodeStrategy {
    /**
     * Determines the total number of immediate children a specific syllabus node has.
     * @param parentPath The materialized path (e.g., "SEC_5" or "SEC_5/SUB_12/CHAP_42")
     * @return Total count of immediate children in the database definition.
     */
    long countTotalChildren(String parentPath);
}