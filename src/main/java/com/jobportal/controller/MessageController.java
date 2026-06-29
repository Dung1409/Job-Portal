package com.jobportal.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.request.SendMessageRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.ConversationResponse;
import com.jobportal.dto.response.MessageResponse;
import com.jobportal.dto.response.PageResponse;
import com.jobportal.entity.User;
import com.jobportal.service.MessageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getConversations(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(messageService.getConversations(user)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MessageResponse>>> getConversation(
            @AuthenticationPrincipal User user,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<MessageResponse> result = messageService.getConversation(user, userId, page, size);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(result)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MessageResponse>> send(
            @Valid @RequestBody SendMessageRequest req,
            @AuthenticationPrincipal User user) {
        MessageResponse msg = messageService.send(req, user);
        broadcastMessage(msg);
        return ResponseEntity.ok(ApiResponse.created(msg));
    }

    @MessageMapping("/messages/send")
    public void sendViaStomp(@AuthenticationPrincipal User user,
                              @Payload SendMessageRequest req) {
        MessageResponse msg = messageService.send(req, user);
        broadcastMessage(msg);
    }

    private void broadcastMessage(MessageResponse msg) {
        messagingTemplate.convertAndSendToUser(
                msg.getReceiverId().toString(), "/queue/messages", msg);
        messagingTemplate.convertAndSendToUser(
                msg.getSenderId().toString(), "/queue/messages", msg);
    }

    @PatchMapping("/read/{fromUserId}")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal User user,
            @PathVariable Long fromUserId) {
        messageService.markAllAsRead(user, fromUserId);
        messagingTemplate.convertAndSendToUser(
                user.getId().toString(), "/queue/messages", "mark-read");
        return ResponseEntity.ok(ApiResponse.ok("Marked as read", null));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(messageService.getUnreadCount(user)));
    }
}
