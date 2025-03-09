package com.yoanesber.spring.security.jwt_auth_postgresql.service;

public interface UserService {
    // to update last login of user
    void updateLastLogin(String userName);
}
