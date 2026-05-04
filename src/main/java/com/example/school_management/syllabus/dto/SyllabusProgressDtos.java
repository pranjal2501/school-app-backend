package com.example.school_management.syllabus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

public class SyllabusProgressDtos {

    @Data
    public static class ToggleRequest {
        private String nodePath; // e.g., "SEC_1/CHAP_2/TOPIC_3"
        private boolean isCompleted;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProgressResponse {
        private Long allocationId;
        // A simple map of path -> true. We only send what IS complete to save bandwidth.
        private Map<String, Boolean> completedNodes;
    }
}