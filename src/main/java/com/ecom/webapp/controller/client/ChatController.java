package com.ecom.webapp.controller.client;


import com.ecom.webapp.model.ChatMessage;
import com.ecom.webapp.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.security.Principal;

@Controller
@CrossOrigin
public class ChatController {

    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        // 1. Xác thực người gửi (lấy từ Principal của Spring Security)
        String senderUsername = principal.getName(); // Lấy username của người dùng đang đăng nhập
        if (!senderUsername.equals(chatMessage.getSenderUsername())) {
            // Xử lý lỗi: người gửi giả mạo? Hoặc cấu hình sai.
            System.err.println("Warning: Sender username mismatch! Principal: " + senderUsername + ", Payload: " + chatMessage.getSenderUsername());
            // Có thể không xử lý tiếp hoặc điều chỉnh senderUsername theo principal
            chatMessage.setSenderUsername(senderUsername); // Đảm bảo sender là người đã login
        }

        // 2. Lưu message vào database
        ChatMessage savedMessage = this.chatMessageService.saveChatMessage(chatMessage);

        // 3. Gửi message đến người nhận cụ thể
        // Spring sẽ tự động chuyển đổi đích đến này thành một destination duy nhất cho user đó
        // Ví dụ: "/user/{recipientUsername}/queue/messages"
        // Client cần subscribe vào "/user/queue/messages"
        messagingTemplate.convertAndSendToUser(
                savedMessage.getRecipientUsername(),
                "/queue/messages", // Đích đến mà client của người nhận lắng nghe
                savedMessage // Gửi message đã lưu (có thể có ID và timestamp từ DB)
        );

        // (Optional) Gửi lại xác nhận hoặc message đã lưu cho chính người gửi
        messagingTemplate.convertAndSendToUser(
                savedMessage.getSenderUsername(),
                "/queue/messages", // Người gửi cũng lắng nghe trên kênh này
                savedMessage
        );
    }

    // Có thể thêm các @MessageMapping khác, ví dụ: xử lý user typing...
    // @MessageMapping("/chat.typing")
    // public void handleTyping(@Payload TypingNotification notification, Principal principal) {
    //     String sender = principal.getName();
    //     messagingTemplate.convertAndSendToUser(notification.getRecipientUsername(), "/queue/typing", sender);
    // }

}
