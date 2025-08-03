package com.yoanesber.backend.jwt_auth_demo.service.impl;

import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.yoanesber.backend.jwt_auth_demo.entity.CustomUserDetails;
import com.yoanesber.backend.jwt_auth_demo.entity.User;
import com.yoanesber.backend.jwt_auth_demo.repository.UserRepository;
import com.yoanesber.backend.jwt_auth_demo.service.CustomUserDetailsService;

/**
 * CustomUserDetailsServiceImpl is a service class that implements the CustomUserDetailsService interface.
 * It provides methods to load user details from the database using the UserRepository.
 * The @Service annotation indicates that this class is a Spring service component.
 */

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
            Hibernate.initialize(user.getRoles());

            // Return the user
            return CustomUserDetails.build(user);
        } else {
            // Throw an exception
            throw new UsernameNotFoundException("User not found with username: " + userName);
        }
    }

}
