package com.ecom.webapp.service.impl;

import com.ecom.webapp.config.oauth2.CustomOAuth2User;
import com.ecom.webapp.model.User;
import com.ecom.webapp.repository.UserRepository;
import com.ecom.webapp.service.OAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService implements OAuth2UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    @Transactional
    @Override
    public OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");

        Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            user = updateExistingUser(user, oAuth2User);
        } else {
            user = registerNewUser(userRequest, oAuth2User);
        }

        return new CustomOAuth2User(oAuth2User, user);
    }

    @Transactional
    protected User registerNewUser(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        User user = new User();

        // Populate user with information from Google
        user.setEmail(oAuth2User.getAttribute("email"));
        user.setFullName(oAuth2User.getAttribute("name"));
        user.setUsername(oAuth2User.getAttribute("email"));
        user.setPassword(passwordEncoder.encode("1111"));
        user.setRole("ROLE_CUSTOMER");
        user.setActive(true);
        user.setPhoneNumber("0111111111");

        // You might want to add more fields like avatar URL etc.
        String pictureUrl = oAuth2User.getAttribute("picture");
        if (pictureUrl != null) {
            user.setAvatar(pictureUrl);
        }
        userRepository.save(user);
        return user;
    }

    @Transactional
    protected User updateExistingUser(User existingUser, OAuth2User oAuth2User) {
        existingUser.setFullName(oAuth2User.getAttribute("name"));

        String pictureUrl = oAuth2User.getAttribute("picture");
        if (pictureUrl != null) {
            existingUser.setAvatar(pictureUrl);
        }
        userRepository.update(existingUser);
        return existingUser;
    }
}
