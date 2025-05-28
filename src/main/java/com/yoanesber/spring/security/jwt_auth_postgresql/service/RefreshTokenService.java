package com.yoanesber.spring.security.jwt_auth_postgresql.service;

import java.util.Optional;

import com.yoanesber.spring.security.jwt_auth_postgresql.entity.RefreshToken;

public interface RefreshTokenService {
    // to find refresh token by token
    Optional<RefreshToken> findByToken(String token);

    // to create refresh token
    RefreshToken createRefreshToken(Long userId);

    // to verify expiration of refresh token
    Boolean isTokenExpired(RefreshToken token);
}
