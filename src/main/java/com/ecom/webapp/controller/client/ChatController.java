package com.ecom.webapp.controller.client;


import com.ecom.webapp.model.ChatMessage;
import com.ecom.webapp.service.ChatMessageService;
import com.ecom.webapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static com.mysql.cj.conf.PropertyKey.logger;

@Controller
@CrossOrigin
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private UserService userService;
//
//    @MessageMapping("/chat.sendMessage")
//    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
//
//        String senderUsername = principal.getName();
//        System.out.println("[DEBUG] Principal username: " + senderUsername);
//        System.out.println("[DEBUG] Payload senderUsername: " + chatMessage.getSenderUsername());
//        System.out.println("[DEBUG] Payload recipientUsername: " + chatMessage.getRecipientUsername());
//        System.out.println("[DEBUG] Payload content: " + chatMessage.getContent());
//
//
//        if (!senderUsername.equals(chatMessage.getSenderUsername())) {
//            // Xử lý lỗi: người gửi giả mạo? Hoặc cấu hình sai.
//            System.err.printf("Warning: Sender username mismatch! Principal: %s , Payload: %s"
//                    ,senderUsername, chatMessage.getSenderUsername());
//            // Có thể không xử lý tiếp hoặc điều chỉnh senderUsername theo principal
//            chatMessage.setSenderUsername(senderUsername); // Đảm bảo sender là người đã login
//        }
//
//
//        // 2. Lưu message vào database
//        ChatMessage savedMessage = this.chatMessageService.saveChatMessage(chatMessage);
//
//        // 3. Gửi message đến người nhận cụ thể
//        // Spring sẽ tự động chuyển đổi đích đến này thành một destination duy nhất cho user đó
//        // Ví dụ: "/user/{recipientUsername}/queue/messages"
//        // Client cần subscribe vào "/user/queue/messages"
//        System.out.println("[INFO] Sending to recipient: " + savedMessage.getRecipientUsername());
//        messagingTemplate.convertAndSendToUser(
//                savedMessage.getRecipientUsername(),
//                "/queue/messages", // Đích đến mà client của người nhận lắng nghe
//                savedMessage // Gửi message đã lưu (có thể có ID và timestamp từ DB)
//        );
//        System.out.println("[INFO] Message sent to recipient.");
//
//        // (Optional) Gửi lại xác nhận hoặc message đã lưu cho chính người gửi
//        messagingTemplate.convertAndSendToUser(
//                savedMessage.getSenderUsername(),
//                "/queue/messages", // Người gửi cũng lắng nghe trên kênh này
//                savedMessage
//        );
//        System.out.println("[INFO] Message sent back to sender.");
//    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        String senderUsername = principal.getName();
        System.out.println("[DEBUG] Principal username: " + senderUsername);
        System.out.println("[DEBUG] Payload senderUsername: " + chatMessage.getSenderUsername());
        System.out.println("[DEBUG] Payload recipientUsername: " + chatMessage.getRecipientUsername());
        System.out.println("[DEBUG] Payload content: " + chatMessage.getContent());
        chatMessage.setSenderUsername(senderUsername); // Đảm bảo sender là người đã login
        Map<String, String> errorPayload = new HashMap<>();
        errorPayload.put("type", "ERROR");
        errorPayload.put("message", "Người nhận không tồn tại: " + chatMessage.getRecipientUsername());
        // Kiểm tra người nhận tồn tại
        if (userService.getUserByUsername(chatMessage.getRecipientUsername())==null) {
            logger.error("Recipient {} does not exist", chatMessage.getRecipientUsername());
            messagingTemplate.convertAndSendToUser(
                    senderUsername,
                    "/queue/errors",
                    errorPayload
            );
            return;
        }
        ChatMessage savedMessage = this.chatMessageService.saveChatMessage(chatMessage);

        // Gửi message đến người nhận
        logger.info("Sending to recipient: {}", savedMessage.getRecipientUsername());
        messagingTemplate.convertAndSendToUser(
                savedMessage.getRecipientUsername(),
                "/queue/messages",
                savedMessage
        );
        System.out.println("[INFO] Message sent to recipient.");
        // Gửi xác nhận cho người gửi
        messagingTemplate.convertAndSendToUser(
                savedMessage.getSenderUsername(),
                "/queue/sent",
                savedMessage
        );
        System.out.println("[INFO] Message sent back to sender.");
    }
    

}
