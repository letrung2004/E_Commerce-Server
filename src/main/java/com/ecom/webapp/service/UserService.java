package com.ecom.webapp.service;

import com.ecom.webapp.model.User;
import com.ecom.webapp.model.dto.UserDto;

import java.util.List;

public interface UserService {
    List<User> getUsers();
    User getUserById(Integer id);
    void save(User user);
    void update(UserDto userDto);
}
