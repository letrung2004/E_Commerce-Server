package com.ecom.webapp.config.oauth2;

import com.ecom.webapp.utils.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();

        try {
            String token = JwtUtils.generateToken(oauthUser.getUsername());
            String redirectUrl = "http://localhost:5173/oauth2/callback?token=" + token; //frontend
//            String redirectUrl = "http://localhost:8080/oauth2/callback?token=" + token;
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            throw new RuntimeException("Could not generate JWT token", e);
        }
    }
}
