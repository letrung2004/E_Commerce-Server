package com.ecom.webapp.service;

import com.ecom.webapp.model.User;
import com.ecom.webapp.model.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User getUserByUsername(String username);
    List<User> getUsers();
    User getUserById(Integer id);
    void save(User user);
    void update(UserDto userDto);
}
