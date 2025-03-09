package com.yoanesber.spring.security.jwt_auth_postgresql.service.impl;

import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.yoanesber.spring.security.jwt_auth_postgresql.entity.CustomUserDetails;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.User;
import com.yoanesber.spring.security.jwt_auth_postgresql.repository.UserRepository;
import com.yoanesber.spring.security.jwt_auth_postgresql.service.CustomUserDetailsService;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Load a user by its username
    @Override
    @Transactional // Enable transaction management 
    public UserDetails loadUserByUsername(String userName) {
        // Check if the username is not null
        Assert.notNull(userName, "Username must not be null");
        
        // Find a user by its username
        User user = userRepository.findByUserName(userName).orElse(null);

        // Check if the user exists
        if (user != null) {
            // Initialize the lazy-loaded roles collection
            Hibernate.initialize(user.getUserRoles());

            // Return the user
            return CustomUserDetails.build(user);
        } else {
            // Throw an exception
            throw new UsernameNotFoundException("User not found with username: " + userName);
        }
    }

}
