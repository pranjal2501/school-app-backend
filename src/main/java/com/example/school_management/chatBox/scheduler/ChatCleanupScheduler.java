package com.example.school_management.chatBox.scheduler;

import com.example.school_management.chatBox.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatCleanupScheduler {

    private final ChatMessageRepository chatRepository;

    // Runs every night at 2:00 AM server time
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanUpOldChats() {
        log.info("Starting scheduled cleanup of old chat messages...");

        // Calculate the exact time 7 days ago
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        // Execute the fast bulk-delete
        chatRepository.deleteMessagesOlderThan(sevenDaysAgo);

        log.info("Successfully deleted chat messages older than {}", sevenDaysAgo);
    }
}