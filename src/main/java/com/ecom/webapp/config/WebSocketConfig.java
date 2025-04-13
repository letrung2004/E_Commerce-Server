package com.ecom.webapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký endpoint "/ws" để client kết nối WebSocket.
        // withSockJS() cung cấp fallback nếu WebSocket không được hỗ trợ.
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
        // setAllowedOriginPatterns("*") cho phép tất cả các origin trong môi trường dev.
        // Trong production, bạn nên giới hạn lại!
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Định nghĩa các tiền tố cho đích đến (destination) mà client sẽ gửi message tới server.
        // Ví dụ: Client gửi message đến /app/chat.sendMessage
        registry.setApplicationDestinationPrefixes("/app");

        // Định nghĩa các tiền tố cho đích đến mà broker sẽ gửi message tới client.
        // /topic: Thường dùng cho pub-sub (chat room công cộng).
        // /queue: Thường dùng cho message 1-1 (nhưng Spring sẽ xử lý /user đặc biệt).
        // enableSimpleBroker sử dụng memory-based message broker.
        // Cũng có thể cấu hình broker bên ngoài như RabbitMQ/ActiveMQ với enableStompBrokerRelay
        registry.enableSimpleBroker("/topic", "/user"); // Kích hoạt /user cho private message

        // Cấu hình tiền tố đích đến cho user-specific messages.
        // Khi bạn gửi message tới "/user/{username}/queue/messages",
        // Spring sẽ tự động định tuyến nó đến user có username đó.
        registry.setUserDestinationPrefix("/user");
    }
}
