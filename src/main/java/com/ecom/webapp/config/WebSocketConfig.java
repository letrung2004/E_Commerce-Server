package com.ecom.webapp.config;

import com.ecom.webapp.service.UserService;
import com.ecom.webapp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserService userDetailsService;


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký endpoint "/ws" để client kết nối WebSocket.
        // withSockJS() cung cấp fallback nếu WebSocket không được hỗ trợ.
        registry.addEndpoint("/ws")
//                .setHandshakeHandler(customHandshakeHandler)
//                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOriginPatterns("*")
                .withSockJS();
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
        registry.enableSimpleBroker("/topic", "/user");  // Kích hoạt /user cho private message

        // Cấu hình tiền tố đích đến cho user-specific messages.
        // Khi bạn gửi message tới "/user/{username}/queue/messages",
        // Spring sẽ tự động định tuyến nó đến user có username đó.
        registry.setUserDestinationPrefix("/user");
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                System.out.println("[INFO] HERER");
//                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//                    String authHeader = accessor.getFirstNativeHeader("Authorization");
//                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                        String token = authHeader.substring(7);
//                        String username = null;
//                        try {
//                            username = jwtUtils.validateTokenAndGetUsername(token);
//                            System.out.println("[INFO] WebSocket user authenticated: " + username);
//                        } catch (Exception e) {
//                            System.err.println("JWT token is invalid: " + e.getMessage());
//                            return null;
//                        }
//                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//                        UsernamePasswordAuthenticationToken authentication =
//                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                        accessor.setUser(authentication); // Gắn vào WebSocket session
//                        SecurityContextHolder.getContext().setAuthentication(authentication); // Optional, nhưng tốt
//                    }
//                }
//
//                return message;
//            }
//        });
//    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(4).maxPoolSize(10);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(128 * 1024);
        registration.setSendBufferSizeLimit(512 * 1024);
        registration.setSendTimeLimit(10 * 1000);
    }
}
