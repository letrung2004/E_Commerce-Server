package com.ecom.webapp.repository;

import com.ecom.webapp.model.User;

import java.util.List;

public interface UserRepository {
    List<User> getUsers();
    User getById(Integer id);
    void save(User user);
    void update(User user);
    User getUserByUsername(String username);
    void delete(User user);
    boolean authenticate(String username, String password);
    boolean existUsername(String username);
    boolean existEmail(String email);
    User findByEmail(String email);
}
