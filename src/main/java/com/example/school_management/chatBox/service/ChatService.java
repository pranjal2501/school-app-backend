package com.example.school_management.chatBox.service;

import com.example.school_management.chatBox.dto.ChatDtos;
import com.example.school_management.chatBox.entity.ChatMessage;
import com.example.school_management.chatBox.repository.ChatMessageRepository;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.enums.SenderRole;
import com.example.school_management.parent.repository.ParentRepository;
import com.example.school_management.student.repository.StudentRepository;
import com.example.school_management.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatRepository;
    private final StudentRepository studentRepository;
    private final RoleVerifier roleVerifier;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;

    @Transactional
    public void sendMessage(ChatDtos.SendMessageRequest request) {

        String loggedInUsername = roleVerifier.getCurrentUsername();

        Long validatedTeacherId;
        String validatedParentMobile;
        SenderRole serverDeterminedRole;

        if (roleVerifier.hasRole(Role.TEACHER)) {
            serverDeterminedRole = SenderRole.TEACHER;

            validatedTeacherId = teacherRepository.findIdByUsername(loggedInUsername)
                    .orElseThrow(() -> new SecurityException("Teacher profile not found."));

            // Target comes from payload
            validatedParentMobile = request.getParentMobile();
        } else if (roleVerifier.hasRole(Role.PARENT)) {
            serverDeterminedRole = SenderRole.PARENT;
            validatedParentMobile = parentRepository.findPrimaryMobileByUsername(loggedInUsername)
                    .orElseThrow(() -> new SecurityException("Parent profile not found."));
            // Target comes from payload (Required in case Parent has siblings in different classes)
            validatedTeacherId = request.getTeacherId();
        } else {
            throw new SecurityException("Unauthorized: Only verified Teachers and Parents can participate in chats.");
        }
        // Even though we fetched their real identity, we STILL verify they are allowed to talk to each other.
        boolean isAuthorized = studentRepository.existsByClassTeacherAndParentMobile(
                validatedTeacherId,
                validatedParentMobile
        );
        if (!isAuthorized) {
            throw new SecurityException("Unauthorized: No valid student-teacher-parent relationship found for this chat.");
        }

        ChatMessage message = ChatMessage.builder()
                .teacherId(validatedTeacherId)
                .parentMobile(validatedParentMobile)
                .senderRole(serverDeterminedRole)
                .content(request.getContent())
                .build();

        chatRepository.save(message);
    }

    public List<ChatDtos.MessageResponse> getChatHistory(Long teacherId, String parentMobile) {
        return chatRepository.findByTeacherIdAndParentMobileOrderByCreatedAtAsc(teacherId, parentMobile)
                .stream()
                .map(msg -> new ChatDtos.MessageResponse(msg.getSenderRole(), msg.getContent(), msg.getCreatedAt()))
                .collect(Collectors.toList());
    }
}