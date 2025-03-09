package com.yoanesber.spring.security.jwt_auth_postgresql.service.impl;

import io.jsonwebtoken.lang.Assert;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yoanesber.spring.security.jwt_auth_postgresql.entity.RefreshToken;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.RefreshTokenId;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.User;
import com.yoanesber.spring.security.jwt_auth_postgresql.repository.RefreshTokenRepository;
import com.yoanesber.spring.security.jwt_auth_postgresql.repository.UserRepository;
import com.yoanesber.spring.security.jwt_auth_postgresql.service.RefreshTokenService;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    @Value("${jwt.refreshTokenExpirationMs}")
    private Long refreshTokenExpirationMs;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository,
        UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        Assert.notNull(token, "Token must not be null");

        // Find refresh token by token
        try {
            return refreshTokenRepository.findByIdToken(token);
        } catch (Exception e) {
            logger.error("Failed to find refresh token by token: " + e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        Assert.notNull(userId, "User id must not be null");

        try {
            // Get user by user id
            User user = userRepository.findById(userId).orElse(null);

            // Check if the user exists
            if (user == null) {
                logger.error("User not found with id: " + userId);
                return null;
            }

            // Get existing refresh token by user id
            RefreshToken existingRefreshToken = refreshTokenRepository.findByUserId(userId);

            // Check if the existing refresh token is not null
            if (existingRefreshToken != null) {
                // Delete the existing refresh token by user id
                refreshTokenRepository.deleteByIdUserId(userId);
            }

            // Create refresh token
            RefreshToken newRefreshToken = new RefreshToken();
            newRefreshToken.setUser(user);
            newRefreshToken.setId(new RefreshTokenId(userId, UUID.randomUUID().toString()));
            newRefreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));
            
            // Save refresh token
            return refreshTokenRepository.save(newRefreshToken);
        } catch (DataIntegrityViolationException e) {
            // If the refresh token already exists, return the existing refresh token
            logger.error("Failed to create refresh token: " + e.getMessage());
            return refreshTokenRepository.findByUserId(userId);
        } catch (Exception e) {
            logger.error("Failed to create refresh token: " + e.getMessage());
            return null;
        }
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        Assert.notNull(token, "Token must not be null");

        try {
            // Check if the refresh token has expired
            if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
                throw new RuntimeException("Refresh token has expired. Please make a new signin request");
            }

            return token;
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify expiration of refresh token: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public int deleteByUserId(Long userId) {
        Assert.notNull(userId, "User id must not be null");

        // Delete refresh token by user id
        try {
            return refreshTokenRepository.deleteByIdUserId(userId);
        } catch (Exception e) {
            logger.error("Failed to delete refresh token by user id: " + e.getMessage());
            return 0;
        }
    }
}
