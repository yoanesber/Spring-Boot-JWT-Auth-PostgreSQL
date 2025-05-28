package com.yoanesber.spring.security.jwt_auth_postgresql.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yoanesber.spring.security.jwt_auth_postgresql.entity.NetflixShows;

/**
 * NetflixShowsRepository is a Spring Data JPA repository interface for the NetflixShows entity.
 * It extends JpaRepository, which provides CRUD operations and pagination support.
 * The @Repository annotation indicates that this interface is a Spring Data repository.
 */

@Repository
public interface NetflixShowsRepository extends JpaRepository<NetflixShows, Long> {
    List<NetflixShows> findAllByIsDeletedFalse(Sort sort);

    NetflixShows findByIdAndIsDeletedFalse(Long id);

    @Override
    default List<NetflixShows> findAll(Sort sort) {
        // Add where clause to filter by isDeleted = false
        return findAllByIsDeletedFalse(sort);
    }

    @Override
    default Optional<NetflixShows> findById(Long id) {
        // Add where clause to filter by isDeleted = false
        return Optional.ofNullable(findByIdAndIsDeletedFalse(id));
    }
}