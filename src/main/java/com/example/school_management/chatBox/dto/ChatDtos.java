package com.example.school_management.chatBox.dto;

import com.example.school_management.common.enums.SenderRole;
import lombok.Data;
import java.time.LocalDateTime;

public class ChatDtos {

    @Data
    public static class SendMessageRequest {
        private Long teacherId;
        private String parentMobile;
        private String content;
    }

    @Data
    public static class MessageResponse {
        private SenderRole senderRole;
        private String content;
        private LocalDateTime timestamp;

        public MessageResponse(SenderRole senderRole, String content, LocalDateTime timestamp) {
            this.senderRole = senderRole;
            this.content = content;
            this.timestamp = timestamp;
        }
    }
}