package com.example.school_management.chatBox.service;

import com.example.school_management.chatBox.dto.ChatDtos;
import com.example.school_management.chatBox.entity.ChatMessage;
import com.example.school_management.chatBox.repository.ChatMessageRepository;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.enums.SenderRole;
import com.example.school_management.parent.repository.ParentRepository;
import com.example.school_management.student.entity.Student;
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

    public List<ChatDtos.ChatInboxResponse> getTeacherInbox(Long teacherId) {
        // Security Check: Ensure the logged-in teacher is fetching their own inbox
        String loggedInUsername = roleVerifier.getCurrentUsername();
        Long sessionTeacherId = teacherRepository.findIdByUsername(loggedInUsername)
                .orElseThrow(() -> new SecurityException("Teacher profile not found."));

        if (!sessionTeacherId.equals(teacherId)) {
            throw new SecurityException("Unauthorized: You cannot access another teacher's inbox.");
        }

        // Fetch the single latest message for each parent chatting with this teacher
        List<ChatMessage> latestMessages = chatRepository.findLatestMessagesForTeacherInbox(teacherId);

        return latestMessages.stream().map(msg -> {
            // Find the student(s) associated with this parent and teacher
            List<Student> students = studentRepository.findStudentsByParentMobileAndClassTeacherId(
                    msg.getParentMobile(), teacherId);

            // Handle edge case if a parent has twins/siblings in the exact same class
            String studentName = students.isEmpty() ? "Unknown Student" :
                    students.stream()
                            .map(s -> s.getFirstName() + " " + s.getLastName())
                            .collect(Collectors.joining(" & "));

            return new ChatDtos.ChatInboxResponse(
                    studentName,
                    msg.getParentMobile(),
                    msg.getContent(),
                    msg.getCreatedAt(),
                    0 // Hardcoded to 0. Add an 'isRead' boolean to ChatMessage entity if you want to calculate this!
            );
        }).collect(Collectors.toList());
    }

    public List<ChatDtos.StudentSearchResponse> searchStudentsForChat(Long teacherId, String query) {
        // Security Check
        String loggedInUsername = roleVerifier.getCurrentUsername();
        Long sessionTeacherId = teacherRepository.findIdByUsername(loggedInUsername)
                .orElseThrow(() -> new SecurityException("Teacher profile not found."));

        if (!sessionTeacherId.equals(teacherId)) {
            throw new SecurityException("Unauthorized: You cannot search another teacher's class roster.");
        }

        // Fetch students matching the query string
        List<Student> searchResults = studentRepository.searchByTeacherAndName(teacherId, query);

        return searchResults.stream().map(student -> new ChatDtos.StudentSearchResponse(
                student.getFirstName() + " " + student.getLastName(),
                student.getParent().getMobilePrimary(), // Adjust getter if your Parent entity uses a different method name
                student.getAssignedClass().getClassName()
        )).collect(Collectors.toList());
    }
}