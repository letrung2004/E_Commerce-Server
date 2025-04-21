package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.ChatMessage;
import com.ecom.webapp.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/secure/chat")
public class ApiChatController {
    @Autowired
    private ChatMessageService chatMessageService;

    @GetMapping("/history/{otherUsername}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @PathVariable("otherUsername") String otherUsername, Principal principal) {
        if (principal == null) {
            // User hasn't logged in
            return ResponseEntity.status(401).build(); // unauthorized
        }
        String currentUsername = principal.getName();
        List<ChatMessage> chatMessagesHistory = this.chatMessageService.getChatMessagesHistory(currentUsername, otherUsername);
        return ResponseEntity.ok(chatMessagesHistory);
    }
}
