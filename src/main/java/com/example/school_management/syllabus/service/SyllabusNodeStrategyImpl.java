package com.example.school_management.syllabus.service;

import com.example.school_management.syllabus.repository.SyllabusChapterRepository;
import com.example.school_management.syllabus.repository.SyllabusSubSectionRepository;
import com.example.school_management.syllabus.repository.SyllabusTopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SyllabusNodeStrategyImpl implements SyllabusNodeStrategy {

    private final SyllabusSubSectionRepository subSectionRepository;
    private final SyllabusChapterRepository chapterRepository;
    private final SyllabusTopicRepository topicRepository;

    @Override
    public long countTotalChildren(String parentPath) {
        if (parentPath == null || parentPath.trim().isEmpty()) {
            return 0;
        }

        try {
            // A path looks like "SEC_1/SUB_2/CHAP_5"
            // The LAST segment dictates what entity we are currently looking at.
            String[] pathSegments = parentPath.split("/");
            String lastSegment = pathSegments[pathSegments.length - 1];

            Long parentId = extractId(lastSegment);

            // Strategy Routing based on Prefix
            if (lastSegment.startsWith("SEC_")) {
                return countChildrenForSection(parentId);
            }
            else if (lastSegment.startsWith("SUB_")) {
                return countChildrenForSubSection(parentId);
            }
            else if (lastSegment.startsWith("CHAP_")) {
                return countChildrenForChapter(parentId);
            }
            else if (lastSegment.startsWith("TOPIC_")) {
                // Topics are the absolute smallest unit (leaf nodes). They have 0 children.
                return 0;
            }

            log.warn("Unknown node prefix in path segment: {}", lastSegment);
            return 0;

        } catch (Exception e) {
            log.error("Failed to count children for path: {}", parentPath, e);
            return 0;
        }
    }

    // ─── PRIVATE HELPER METHODS ────────────────────────────────────────────────

    private Long extractId(String segment) {
        // e.g., converts "CHAP_42" to the Long value 42
        String[] parts = segment.split("_");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid segment format: " + segment);
        }
        return Long.parseLong(parts[1]);
    }

    private long countChildrenForSection(Long sectionId) {
        // A Section is unique because it can have BOTH Sub-Sections and Direct Chapters.
        // We must add them together to get the total children required to complete the section.
        long subSectionCount = subSectionRepository.countBySectionId(sectionId);
        long directChapterCount = chapterRepository.countBySectionId(sectionId);

        return subSectionCount + directChapterCount;
    }

    private long countChildrenForSubSection(Long subSectionId) {
        // Sub-Sections only contain Chapters
        return chapterRepository.countBySubSectionId(subSectionId);
    }

    private long countChildrenForChapter(Long chapterId) {
        // Chapters only contain Topics
        return topicRepository.countByChapterId(chapterId);
    }
}