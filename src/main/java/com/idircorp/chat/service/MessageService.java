package com.idircorp.chat.service;

import com.idircorp.chat.entity.*;
import com.idircorp.chat.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    public Message createMessage(Long conversationId, Long senderId, String type, String text, MultipartFile file) throws IOException {
        Conversation conversation = conversationRepository.findById(conversationId).orElseThrow();
        User sender = userRepository.findById(senderId).orElseThrow();

        Content content;
        if ("text".equals(type)) {
            content = new TextContent();
            ((TextContent) content).setText(text);
        } else {
            content = new FileContent();
            ((FileContent) content).setFile(file.getBytes());
            ((FileContent) content).setFileName(file.getOriginalFilename());
            ((FileContent) content).setFileType(file.getContentType());
        }

        content.setType(type);

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        return messageRepository.save(message);
    }
}