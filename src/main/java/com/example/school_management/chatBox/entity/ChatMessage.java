package com.example.school_management.chatBox.entity;

import com.example.school_management.common.enums.SenderRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages", indexes = {
        @Index(name = "idx_chat_thread", columnList = "teacher_id, parent_mobile")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(name = "parent_mobile", nullable = false, length = 15)
    private String parentMobile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SenderRole senderRole;

    @Column(nullable = false, length = 1000)
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}