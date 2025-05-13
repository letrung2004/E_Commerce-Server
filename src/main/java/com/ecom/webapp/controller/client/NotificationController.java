package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.ChatMessage;
import com.ecom.webapp.model.User;
import com.ecom.webapp.model.dto.ChatMessageDto;
import com.ecom.webapp.model.dto.OrderDto;
import com.ecom.webapp.model.dto.OrderNotificationDto;
import com.ecom.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class NotificationController {

    @Autowired
    private UserService userService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/sendMessage")
    @SendTo("/topic/notifications")
    public String sendMessage(@Payload String chatMessage, Principal principal) {
        String senderUsername = principal.getName();
        System.out.println("Principal username: " + senderUsername);
        System.out.println("Payload content: " + chatMessage);

        return chatMessage;

    }

    @MessageMapping("/sendPrivateMessage")
    public void sendPrivateMessage(@Payload OrderNotificationDto orderNotificationDto, Principal principal) {
        System.out.println("OrderDto notification: " + orderNotificationDto);
        String recipient = orderNotificationDto.getStoreUsername();
        System.out.println("Send from " + principal.getName() + " to " + recipient + ": " + orderNotificationDto.getStoreUsername());
        User recipUser = userService.getUserByUsername(recipient);
        if (recipUser == null) {
            System.out.println("Recipient user not found");

        } else
            System.out.println("Recipient user Found: " + recipUser);
        // Gửi đến user cụ thể
        messagingTemplate.convertAndSendToUser(recipient, "/queue/messages", orderNotificationDto);

    }

//    @MessageMapping("/sendPrivateMessage")
//    public void sendPrivateMessage(@Payload ChatMessageDto message, Principal principal) {
//        System.out.println("MessageDto: " + message);
//        String recipient = message.getRecipientUsername();
//        System.out.println("Send from " + principal.getName() + " to " + recipient + ": " + message.getContent());
//        User recipUser = userService.getUserByUsername(recipient);
//        if (recipUser == null) {
//            System.out.println("Recipient user not found");
//
//        } else
//            System.out.println("Recipient user Found: " + recipUser);
//        // Gửi đến user cụ thể
//        messagingTemplate.convertAndSendToUser(recipient, "/queue/messages", message);
//
//    }

}