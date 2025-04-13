package com.ecom.webapp.service.impl;

import com.ecom.webapp.model.ChatMessage;
import com.ecom.webapp.model.User;
import com.ecom.webapp.repository.ChatMessageRepository;
import com.ecom.webapp.repository.UserRepository;
import com.ecom.webapp.service.ChatMessageService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<ChatMessage> getChatMessagesHistory(String senderUsername, String recipientUsername) {
        User sender = this.userRepository.getUserByUsername(senderUsername);
        if (sender == null) {
            throw new EntityNotFoundException("Sender not found with name " + senderUsername);
        }
        User recipient = this.userRepository.getUserByUsername(recipientUsername);
        if (recipient == null) {
            throw new EntityNotFoundException("Recipient not found with name " + recipientUsername);
        }

        return this.chatMessageRepository.getChatMessagesHistory(sender, recipient);
    }

    @Override
    public ChatMessage saveChatMessage(ChatMessage chatMessage) {
        return this.chatMessageRepository.saveChatMessage(chatMessage);
    }
}
