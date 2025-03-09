package com.yoanesber.spring.security.jwt_auth_postgresql.service.impl;

import io.jsonwebtoken.lang.Assert;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yoanesber.spring.security.jwt_auth_postgresql.entity.User;
import com.yoanesber.spring.security.jwt_auth_postgresql.repository.UserRepository;
import com.yoanesber.spring.security.jwt_auth_postgresql.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void updateLastLogin(String userName) {
        Assert.notNull(userName, "Username must not be null");
        
        try {
            // Find a user by its username
            User user = userRepository.findByUserName(userName).orElse(null);

            // Check if the user exists
            if (user != null) {
                user.setLastLogin(Instant.now());
                userRepository.save(user);
            } else {
                throw new RuntimeException("User not found with username: " + userName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update last login: " + e.getMessage());
        }
    }
}
