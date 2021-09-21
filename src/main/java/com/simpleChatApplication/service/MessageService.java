package com.simpleChatApplication.service;


import com.simpleChatApplication.model.Message;
import com.simpleChatApplication.repository.MessageRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderInitializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class MessageService {
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    private SimpMessagingTemplate template;



    public Long save(Message message) {
        return messageRepository.save(message).getId();

    }

    //Broadcasting the message to subscribers of chat topic
    @MessageMapping("/chatapp")
    public void send(Message message, Long conversationId) throws IOException { //@DestinationVariable
        template.convertAndSend("/topic/messages/" + conversationId, message);
        System.out.println("Sending to : /topic/messages/" + conversationId);
    }

    // Only a method that uses the two methods (getChatMessagesIds, getChatMessages)
    public Collection<Message> getChat(Long senderId, Long receiverId) {
        Collection<Long> messagesIds = getChatMessagesIds(senderId, receiverId);
        return getChatMessages(messagesIds);
    }

    //Returns a list of message ids that is retrieved from Conversation table by using the senderId and the receiverId
    public Collection<Long> getChatMessagesIds(Long senderId, Long receiverId) {
        return messageRepository.getChatMessagesIds(senderId, receiverId);
    }

    //Returns a list of message objects that is retrieved from message table by using message ids
    public Collection<Message> getChatMessages(Collection<Long> messagesIds) {
        return messageRepository.findAllById(messagesIds);
    }

}
