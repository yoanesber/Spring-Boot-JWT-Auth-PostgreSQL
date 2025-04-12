package com.yoanesber.spring.security.jwt_auth_postgresql.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.yoanesber.spring.security.jwt_auth_postgresql.entity.RefreshToken;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.RefreshTokenId;

/**
 * RefreshTokenRepository is a Spring Data JPA repository interface for the RefreshToken entity.
 * It extends JpaRepository, which provides CRUD operations and pagination support.
 * The @Repository annotation indicates that this interface is a Spring Data repository.
 */

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, RefreshTokenId> {
    // Find a refresh token by its token
    Optional<RefreshToken> findByIdToken(String token);

    // Find a refresh token by its user id
    RefreshToken findByUserId(Long userId);

    // Delete a refresh token by its user id
    @Modifying(clearAutomatically = true, flushAutomatically = true) // Clear and flush the entity manager after the query
    @Query("DELETE FROM RefreshToken rt WHERE rt.user.id = :userId")
    int deleteByIdUserId(Long userId);
}
