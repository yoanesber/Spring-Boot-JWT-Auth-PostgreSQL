package com.yoanesber.backend.jwt_auth_demo.service;

import java.util.List;

import com.yoanesber.backend.jwt_auth_demo.dto.NetflixShowsDTO;

public interface NetflixShowsService {
    // Create NetflixShows
    NetflixShowsDTO createNetflixShows(NetflixShowsDTO netflixShowsDTO);

    // Get all NetflixShows
    List<NetflixShowsDTO> getAllNetflixShows();

    // Get NetflixShows by id
    NetflixShowsDTO getNetflixShowsById(Long id);

    // Update NetflixShows
    NetflixShowsDTO updateNetflixShows(Long id, NetflixShowsDTO netflixShowsDTO);

    // Delete NetflixShows
    Boolean deleteNetflixShows(Long id);
}
