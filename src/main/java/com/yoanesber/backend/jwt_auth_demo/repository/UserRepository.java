package com.yoanesber.backend.jwt_auth_demo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yoanesber.backend.jwt_auth_demo.entity.User;

/**
 * UserRepository is a Spring Data JPA repository interface for the User entity.
 * It extends JpaRepository, which provides CRUD operations and pagination support.
 * The @Repository annotation indicates that this interface is a Spring Data repository.
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Find a user by its username
    Optional<User> findByUserName(String userName);

    // Check if a user exists by its username
    Boolean existsByUserName(String userName);

    // Check if a user exists by its email
    Boolean existsByEmail(String email);
}
