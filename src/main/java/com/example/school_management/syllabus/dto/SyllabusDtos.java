package com.example.school_management.syllabus.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

public class SyllabusDtos {

    @Data
    public static class SyllabusPayload {
        private Long id;
        private List<SectionDto> sections = new ArrayList<>();
    }

    @Data
    public static class SectionDto {
        private Long id;
        private String name;
        private Integer orderIndex;
        private List<SubSectionDto> subSections = new ArrayList<>();
        private List<ChapterDto> directChapters = new ArrayList<>();
    }

    @Data
    public static class SubSectionDto {
        private Long id;
        private String name;
        private Integer orderIndex;
        private List<ChapterDto> chapters = new ArrayList<>();
    }

    @Data
    public static class ChapterDto {
        private Long id;
        private String name;
        private Integer orderIndex;
        private List<TopicDto> topics = new ArrayList<>();
    }

    @Data
    public static class TopicDto {
        private Long id;
        private String name;
        private Integer orderIndex;
    }
}