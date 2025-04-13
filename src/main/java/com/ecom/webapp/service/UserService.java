package com.ecom.webapp.service;

import com.ecom.webapp.model.User;
import com.ecom.webapp.model.dto.UserDto;
import com.ecom.webapp.model.responseDto.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User getUserByUsername(String username);
    UserResponse getUserResponseByUsername(String username);
    List<User> getUsers();
    User getUserById(Integer id);
    void createUser(UserDto user, String username, String rawPassword);
    void update(UserDto userDto);
    void acceptStoreActivation(int userId);
    void deleteUser(Integer id);
    boolean authenticate(String username, String password);
}
