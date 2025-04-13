package com.ecom.webapp.repository;

import com.ecom.webapp.model.ChatMessage;
import com.ecom.webapp.model.User;

import java.util.List;

public interface ChatMessageRepository {
    List<ChatMessage> getChatMessagesHistory(User sender, User recipient);
    ChatMessage saveChatMessage(ChatMessage chatMessage);
}
