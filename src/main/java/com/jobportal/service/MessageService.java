package com.jobportal.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobportal.dto.request.SendMessageRequest;
import com.jobportal.dto.response.ConversationResponse;
import com.jobportal.dto.response.MessageResponse;
import com.jobportal.entity.Message;
import com.jobportal.entity.User;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.MessageRepository;
import com.jobportal.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepo;
    private final UserRepository userRepo;

    @Transactional
    public MessageResponse send(SendMessageRequest req, User sender) {
        User receiver = userRepo.findById(req.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        Message msg = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .jobId(req.getJobId())
                .content(req.getContent())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(messageRepo.save(msg));
    }

    @Transactional(readOnly = true)
    public Page<MessageResponse> getConversation(User currentUser, Long otherUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepo.findConversation(currentUser.getId(), otherUserId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> getConversations(User currentUser) {
        List<Long> partnerIds = messageRepo.findConversationPartnerIds(currentUser.getId());
        List<ConversationResponse> result = new ArrayList<>();

        for (Long partnerId : partnerIds) {
            User partner = userRepo.findById(partnerId).orElse(null);
            if (partner == null) continue;

            Message lastMsg = messageRepo.findLastMessageBetweenUsers(currentUser.getId(), partnerId);
            long unread = messageRepo.countUnreadFromSender(currentUser.getId(), partnerId);

            result.add(ConversationResponse.builder()
                    .userId(partner.getId())
                    .fullName(partner.getFullName())
                    .email(partner.getEmail())
                    .lastMessage(lastMsg != null ? lastMsg.getContent() : null)
                    .lastMessageId(lastMsg != null ? lastMsg.getId() : null)
                    .lastMessageIsRead(lastMsg != null ? lastMsg.isRead() : true)
                    .lastMessageAt(lastMsg != null ? lastMsg.getCreatedAt() : null)
                    .unreadCount((int) unread)
                    .build());
        }

        result.sort((a, b) -> {
            if (a.getLastMessageAt() == null && b.getLastMessageAt() == null) return 0;
            if (a.getLastMessageAt() == null) return 1;
            if (b.getLastMessageAt() == null) return -1;
            return b.getLastMessageAt().compareTo(a.getLastMessageAt());
        });

        return result;
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(User currentUser) {
        return messageRepo.countUnreadByReceiverId(currentUser.getId());
    }

    @Transactional
    public void markAllAsRead(User currentUser, Long fromUserId) {
        messageRepo.markAllAsRead(currentUser.getId(), fromUserId);
    }

    private MessageResponse toResponse(Message msg) {
        return MessageResponse.builder()
                .id(msg.getId())
                .senderId(msg.getSender().getId())
                .senderName(msg.getSender().getFullName())
                .senderEmail(msg.getSender().getEmail())
                .receiverId(msg.getReceiver().getId())
                .receiverName(msg.getReceiver().getFullName())
                .receiverEmail(msg.getReceiver().getEmail())
                .jobId(msg.getJobId())
                .content(msg.getContent())
                .isRead(msg.isRead())
                .createdAt(msg.getCreatedAt())
                .build();
    }
}
