package com.ecom.webapp.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {

        System.out.println("[Handshake Handler] --- Bắt đầu determineUser ---");
        System.out.println("[Handshake Handler] Attributes received: " + attributes); // Log toàn bộ attributes

        // Lấy username từ attributes do WebSocketAuthInterceptor đặt
        String username = (String) attributes.get("username");

        System.out.println("[Handshake Handler] Username from attributes: " + username);

        Principal principal;
        if (username != null) {
            principal = new StompPrincipal(username);
            System.out.println("[Handshake Handler] Created StompPrincipal for user: " + username);
        } else {
            principal = new StompPrincipal("anonymous-" + UUID.randomUUID());
            System.out.println("[Handshake Handler] Username not found in attributes. Created anonymous Principal: " + principal.getName());
        }

        System.out.println("[Handshake Handler] Returning Principal: " + principal.getName()); // Log Principal cuối cùng
        System.out.println("[Handshake Handler] --- Kết thúc determineUser ---");

        return principal;
    }
}
