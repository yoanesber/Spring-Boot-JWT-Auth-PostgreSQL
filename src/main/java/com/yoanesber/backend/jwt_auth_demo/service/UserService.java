package com.yoanesber.backend.jwt_auth_demo.service;

public interface UserService {
    // to update last login of user
    void updateLastLogin(String userName);
}
