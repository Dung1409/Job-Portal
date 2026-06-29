package com.jobportal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.jobportal.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
           "(m.sender.id = :userId1 AND m.receiver.id = :userId2) OR " +
           "(m.sender.id = :userId2 AND m.receiver.id = :userId1) " +
           "ORDER BY m.createdAt DESC")
    Page<Message> findConversation(@Param("userId1") Long userId1,
                                   @Param("userId2") Long userId2,
                                   Pageable pageable);

    @Query(value = "SELECT DISTINCT sender_id FROM messages WHERE receiver_id = :userId " +
           "UNION " +
           "SELECT DISTINCT receiver_id FROM messages WHERE sender_id = :userId",
           nativeQuery = true)
    List<Long> findConversationPartnerIds(@Param("userId") Long userId);

    @Query("SELECT m FROM Message m WHERE " +
           "((m.sender.id = :userId1 AND m.receiver.id = :userId2) OR " +
           "(m.sender.id = :userId2 AND m.receiver.id = :userId1)) " +
           "AND m.createdAt = (SELECT MAX(m2.createdAt) FROM Message m2 WHERE " +
           "(m2.sender.id = :userId1 AND m2.receiver.id = :userId2) OR " +
           "(m2.sender.id = :userId2 AND m2.receiver.id = :userId1))")
    Message findLastMessageBetweenUsers(@Param("userId1") Long userId1,
                                        @Param("userId2") Long userId2);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.id = :receiverId AND m.isRead = false")
    long countUnreadByReceiverId(@Param("receiverId") Long receiverId);

    @Query("SELECT COUNT(m) FROM Message m WHERE " +
           "m.receiver.id = :receiverId AND m.sender.id = :senderId AND m.isRead = false")
    long countUnreadFromSender(@Param("receiverId") Long receiverId,
                               @Param("senderId") Long senderId);

    @Transactional
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE " +
           "m.receiver.id = :receiverId AND m.sender.id = :senderId AND m.isRead = false")
    void markAllAsRead(@Param("receiverId") Long receiverId,
                       @Param("senderId") Long senderId);
}
