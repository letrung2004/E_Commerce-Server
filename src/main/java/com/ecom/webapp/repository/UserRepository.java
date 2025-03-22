package com.ecom.webapp.repository;

import com.ecom.webapp.model.User;

public interface UserRepository {
    User getUserByUsername(String username);
}
