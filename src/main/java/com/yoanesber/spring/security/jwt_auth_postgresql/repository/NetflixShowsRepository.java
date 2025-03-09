package com.yoanesber.spring.security.jwt_auth_postgresql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yoanesber.spring.security.jwt_auth_postgresql.entity.NetflixShows;

@Repository
public interface NetflixShowsRepository extends JpaRepository<NetflixShows, Long> {}