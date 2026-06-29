package com.jobportal.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
    private Long userId;
    private String fullName;
    private String email;
    private String lastMessage;
    private Long lastMessageId;
    private boolean lastMessageIsRead;
    private LocalDateTime lastMessageAt;
    private int unreadCount;
}
