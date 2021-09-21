package com.simpleChatApplication.service;

import com.simpleChatApplication.model.Conversation;
import com.simpleChatApplication.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConversationService {
    @Autowired
    ConversationRepository conversationRepository;

    public void save(Long convId, Long messageId, Long senderId, Long receiverId) {
        conversationRepository.save(new Conversation(convId, messageId, senderId, receiverId));
    }

    public Long getConversationId(Long senderId, Long receiverId) {
        Long conversationId = conversationRepository.getConversationId(senderId, receiverId);
        if (conversationId == null) {
            Long maxConversationId = conversationRepository.getMaxConversationId();
            if (maxConversationId == null)
                conversationId = Long.valueOf(1);
            else
                conversationId = ++maxConversationId;
        }
        return conversationId;
    }
    public Long getConversationsCount(){
        return conversationRepository.getConversationsCount();
    }


}
