package com.simpleChatApplication.repository;

import com.simpleChatApplication.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public interface ConversationRepository extends JpaRepository<Conversation,Long> {
    @Query(value = "select distinct conv_id from chatapp.conversation where sender_id= :sender and receiver_id= :receiver or " +
            "sender_id= :receiver and receiver_id= :sender",nativeQuery = true)
    public Long getConversationId(@Param("sender") Long senderId, @Param("receiver") Long receiverId);

    @Query(value = "select max(conv_id) from chatapp.conversation",nativeQuery = true)
    public Long getMaxConversationId();

    @Query(value = "select count(distinct conv_id) from chatapp.conversation",nativeQuery = true)
    public Long getConversationsCount();
}
