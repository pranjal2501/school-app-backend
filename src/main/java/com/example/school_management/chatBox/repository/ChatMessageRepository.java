package com.example.school_management.chatBox.repository;

import com.example.school_management.chatBox.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Fetch the chat thread in chronological order
    List<ChatMessage> findByTeacherIdAndParentMobileOrderByCreatedAtAsc(Long teacherId, String parentMobile);

    @Modifying
    @Query("DELETE FROM ChatMessage c WHERE c.createdAt <= :cutoffDate")
    void deleteMessagesOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Query("SELECT m FROM ChatMessage m WHERE m.teacherId = :teacherId AND m.id IN " +
            "(SELECT MAX(m2.id) FROM ChatMessage m2 WHERE m2.teacherId = :teacherId GROUP BY m2.parentMobile) " +
            "ORDER BY m.createdAt DESC")
    List<ChatMessage> findLatestMessagesForTeacherInbox(@Param("teacherId") Long teacherId);
}