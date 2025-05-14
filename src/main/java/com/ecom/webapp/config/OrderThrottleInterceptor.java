package com.ecom.webapp.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderThrottleInterceptor implements HandlerInterceptor {
    private final Map<String, Long> lastTs = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse resp, Object handler) throws IOException {
        if (!request.getRequestURI().endsWith("/place-order")) return true;
        String user = request.getUserPrincipal().getName();
        long now = System.currentTimeMillis();
        Long prev = lastTs.get(user);
        if (prev != null && now - prev < 5000) {
            resp.setStatus(429);
            resp.getWriter().write("Chờ 5 giây trước khi đặt lại.");
            return false;
        }
        lastTs.put(user, now);
        return true;
    }
}