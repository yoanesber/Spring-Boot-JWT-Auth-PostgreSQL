package com.yoanesber.spring.security.jwt_auth_postgresql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yoanesber.spring.security.jwt_auth_postgresql.entity.NetflixShows;

/**
 * NetflixShowsRepository is a Spring Data JPA repository interface for the NetflixShows entity.
 * It extends JpaRepository, which provides CRUD operations and pagination support.
 * The @Repository annotation indicates that this interface is a Spring Data repository.
 */

@Repository
public interface NetflixShowsRepository extends JpaRepository<NetflixShows, Long> {}