package com.ecom.webapp.service;

import com.ecom.webapp.model.User;
import com.ecom.webapp.model.dto.UserDto;
import com.ecom.webapp.model.responseDto.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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
    User registerUser(Map<String, String> params, MultipartFile avatar);
}
