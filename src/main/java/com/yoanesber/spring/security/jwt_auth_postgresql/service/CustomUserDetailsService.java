package com.yoanesber.spring.security.jwt_auth_postgresql.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomUserDetailsService extends UserDetailsService {
    // Load a user by its username
    UserDetails loadUserByUsername(String userName);
}
