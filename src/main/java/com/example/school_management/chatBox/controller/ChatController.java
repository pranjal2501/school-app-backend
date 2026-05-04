package com.example.school_management.chatBox.controller;

import com.example.school_management.chatBox.dto.ChatDtos;
import com.example.school_management.chatBox.service.ChatService;
import com.example.school_management.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // Endpoint to send a message
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendMessage(@RequestBody ChatDtos.SendMessageRequest request) {
        try {
            chatService.sendMessage(request);
            return ResponseEntity.ok(ApiResponse.ok("Message sent successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to send message: " + e.getMessage()));
        }
    }

    // Endpoint to fetch history
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<ChatDtos.MessageResponse>>> getChatHistory(
            @RequestParam Long teacherId,
            @RequestParam String parentMobile) {

        try {
            List<ChatDtos.MessageResponse> history = chatService.getChatHistory(teacherId, parentMobile);
            return ResponseEntity.ok(ApiResponse.ok("Chat history fetched successfully", history));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to fetch chat history: " + e.getMessage()));
        }
    }

    @GetMapping("/teacher/inbox")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<ChatDtos.ChatInboxResponse>>> getTeacherInbox(
            @RequestParam Long teacherId) {
        try {
            List<ChatDtos.ChatInboxResponse> inbox = chatService.getTeacherInbox(teacherId);
            return ResponseEntity.ok(ApiResponse.ok("Inbox fetched successfully", inbox));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to fetch inbox: " + e.getMessage()));
        }
    }

    @GetMapping("/teacher/search-students")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<ChatDtos.StudentSearchResponse>>> searchStudentsForChat(
            @RequestParam Long teacherId,
            @RequestParam String query) {
        try {
            List<ChatDtos.StudentSearchResponse> students = chatService.searchStudentsForChat(teacherId, query);
            return ResponseEntity.ok(ApiResponse.ok("Students fetched successfully", students));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Search failed: " + e.getMessage()));
        }
    }
}