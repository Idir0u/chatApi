package com.idircorp.chat.service;

import com.idircorp.chat.entity.Conversation;
import com.idircorp.chat.entity.User;
import com.idircorp.chat.repository.ConversationRepository;
import com.idircorp.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    public Conversation createConversation(List<Long> userIds, boolean isGroupChat) {
        List<User> users = userRepository.findAllById(userIds);
        Conversation conversation = new Conversation();
        conversation.setUsers(users);
        conversation.setGroupChat(isGroupChat);
        return conversationRepository.save(conversation);
    }

    public Optional<Conversation> getConversationById(Long id) {
        return conversationRepository.findById(id);
    }

    public List<Conversation> getAllConversations() {
        return conversationRepository.findAll();
    }

    public void deleteConversation(Long id) {
        conversationRepository.deleteById(id);
    }
}