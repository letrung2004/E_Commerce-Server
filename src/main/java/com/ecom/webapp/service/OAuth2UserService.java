package com.ecom.webapp.service;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2UserService {
    OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User);
}
