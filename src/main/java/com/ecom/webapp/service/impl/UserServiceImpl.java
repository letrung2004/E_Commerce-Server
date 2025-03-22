package com.ecom.webapp.service.impl;

import com.ecom.webapp.model.User;
import com.ecom.webapp.model.dto.UserDto;
import com.ecom.webapp.repository.UserRepository;
import com.ecom.webapp.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("userDetailsService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUserByUsername(String username) {
        return this.userRepository.getUserByUsername(username);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = this.getUserByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("Invalid User!");
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(u.getRole()));
        return new org.springframework.security.core.userdetails.User(
                u.getUsername(), u.getPassword(), authorities);
    }


    @Override
    public List<User> getUsers() {
        return this.userRepository.getUsers();
    }

    @Override
    public User getUserById(Integer id) {
        return this.userRepository.getById(id);
    }

    @Override
    public void save(User user) {
        this.userRepository.save(user);
    }

    @Override
    public void update(UserDto userDto) {
        User user = this.userRepository.getById(userDto.getId());

        if (user == null) {
            throw new EntityNotFoundException("User not found with id " + userDto.getId());
        }
        user.setFullName(userDto.getFullName());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(String.valueOf(userDto.getPhoneNumber()));
        user.setGender(userDto.getGender());
        user.setRole(userDto.getRole());
        user.setDateOfBirth(userDto.getDateOfBirth());
        this.userRepository.update(user);
    }
}

