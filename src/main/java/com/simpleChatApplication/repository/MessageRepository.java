package com.simpleChatApplication.repository;

import com.simpleChatApplication.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.Collection;
@Component
public interface MessageRepository extends JpaRepository<Message,Long> {
    @Query(value = "select * from chatapp.conversation where sender_id= :sender and receiver_id= :receiver or" +
            " sender_id= :receiver and receiver_id= :sender ",nativeQuery = true)
    public Collection<Long> getChatMessagesIds(@Param("sender") Long senderId, @Param("receiver") Long receiverId);



}
