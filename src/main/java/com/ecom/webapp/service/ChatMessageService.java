package com.ecom.webapp.service;

import com.ecom.webapp.model.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    List<ChatMessage> getChatMessagesHistory(String senderUsername, String recipientUsername);
    ChatMessage saveChatMessage(ChatMessage chatMessage);
}
