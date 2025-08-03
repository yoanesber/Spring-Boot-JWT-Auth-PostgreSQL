package com.yoanesber.backend.jwt_auth_demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomUserDetailsService extends UserDetailsService {
    // Load a user by its username
    UserDetails loadUserByUsername(String userName);
}
