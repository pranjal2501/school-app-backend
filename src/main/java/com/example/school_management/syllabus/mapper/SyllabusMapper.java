package com.example.school_management.syllabus.mapper;

import com.example.school_management.syllabus.dto.SyllabusDtos.*;
import com.example.school_management.syllabus.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SyllabusMapper {

    // ==========================================
    // 1. ENTITY -> DTO (FOR GET ENDPOINT)
    // ==========================================
    public SyllabusPayload toPayload(Syllabus syllabus) {
        SyllabusPayload payload = new SyllabusPayload();
        payload.setId(syllabus.getId());

        payload.setSections(syllabus.getSections().stream().map(this::toSectionDto).collect(Collectors.toList()));
        return payload;
    }

    private SectionDto toSectionDto(SyllabusSection section) {
        SectionDto dto = new SectionDto();
        dto.setId(section.getId());
        dto.setName(section.getName());
        dto.setOrderIndex(section.getOrderIndex());
        dto.setSubSections(section.getSubSections().stream().map(this::toSubSectionDto).collect(Collectors.toList()));
        dto.setDirectChapters(section.getDirectChapters().stream().map(this::toChapterDto).collect(Collectors.toList()));
        return dto;
    }

    private SubSectionDto toSubSectionDto(SyllabusSubSection subSection) {
        SubSectionDto dto = new SubSectionDto();
        dto.setId(subSection.getId());
        dto.setName(subSection.getName());
        dto.setOrderIndex(subSection.getOrderIndex());
        dto.setChapters(subSection.getChapters().stream().map(this::toChapterDto).collect(Collectors.toList()));
        return dto;
    }

    private ChapterDto toChapterDto(SyllabusChapter chapter) {
        ChapterDto dto = new ChapterDto();
        dto.setId(chapter.getId());
        dto.setName(chapter.getName());
        dto.setOrderIndex(chapter.getOrderIndex());
        dto.setTopics(chapter.getTopics().stream().map(this::toTopicDto).collect(Collectors.toList()));
        return dto;
    }

    private TopicDto toTopicDto(SyllabusTopic topic) {
        TopicDto dto = new TopicDto();
        dto.setId(topic.getId());
        dto.setName(topic.getName());
        dto.setOrderIndex(topic.getOrderIndex());
        return dto;
    }

    // ==========================================
    // 2. DTO -> ENTITY MERGE (FOR PUT ENDPOINT)
    // ==========================================
    public void mergeSections(Syllabus syllabus, List<SectionDto> dtos) {
        // Map existing sections by ID so we don't duplicate them
        Map<Long, SyllabusSection> existingMap = syllabus.getSections().stream()
                .filter(s -> s.getId() != null)
                .collect(Collectors.toMap(SyllabusSection::getId, s -> s));

        // Clear the current list (Hibernate won't delete them if we add them right back)
        syllabus.getSections().clear();

        for (SectionDto dto : dtos) {
            SyllabusSection section = existingMap.getOrDefault(dto.getId(), new SyllabusSection());
            section.setName(dto.getName());
            section.setOrderIndex(dto.getOrderIndex());
            section.setSyllabus(syllabus);

            mergeSubSections(section, dto.getSubSections());
            mergeChapters(section, null, dto.getDirectChapters(), section.getDirectChapters());

            syllabus.getSections().add(section);
        }
    }

    private void mergeSubSections(SyllabusSection section, List<SubSectionDto> dtos) {
        Map<Long, SyllabusSubSection> existingMap = section.getSubSections().stream()
                .filter(s -> s.getId() != null)
                .collect(Collectors.toMap(SyllabusSubSection::getId, s -> s));

        section.getSubSections().clear();

        for (SubSectionDto dto : dtos) {
            SyllabusSubSection subSection = existingMap.getOrDefault(dto.getId(), new SyllabusSubSection());
            subSection.setName(dto.getName());
            subSection.setOrderIndex(dto.getOrderIndex());
            subSection.setSection(section);

            mergeChapters(null, subSection, dto.getChapters(), subSection.getChapters());

            section.getSubSections().add(subSection);
        }
    }

    private void mergeChapters(SyllabusSection section, SyllabusSubSection subSection, List<ChapterDto> dtos, List<SyllabusChapter> targetList) {
        Map<Long, SyllabusChapter> existingMap = targetList.stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(SyllabusChapter::getId, c -> c));

        targetList.clear();

        for (ChapterDto dto : dtos) {
            SyllabusChapter chapter = existingMap.getOrDefault(dto.getId(), new SyllabusChapter());
            chapter.setName(dto.getName());
            chapter.setOrderIndex(dto.getOrderIndex());
            chapter.setSection(section);
            chapter.setSubSection(subSection);

            mergeTopics(chapter, dto.getTopics());

            targetList.add(chapter);
        }
    }

    private void mergeTopics(SyllabusChapter chapter, List<TopicDto> dtos) {
        Map<Long, SyllabusTopic> existingMap = chapter.getTopics().stream()
                .filter(t -> t.getId() != null)
                .collect(Collectors.toMap(SyllabusTopic::getId, t -> t));

        chapter.getTopics().clear();

        for (TopicDto dto : dtos) {
            SyllabusTopic topic = existingMap.getOrDefault(dto.getId(), new SyllabusTopic());
            topic.setName(dto.getName());
            topic.setOrderIndex(dto.getOrderIndex());
            topic.setChapter(chapter);

            chapter.getTopics().add(topic);
        }
    }
}