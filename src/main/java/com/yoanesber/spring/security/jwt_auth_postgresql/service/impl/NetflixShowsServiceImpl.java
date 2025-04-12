package com.yoanesber.spring.security.jwt_auth_postgresql.service.impl;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.yoanesber.spring.security.jwt_auth_postgresql.dto.NetflixShowsDTO;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.EShowType;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.NetflixShows;
import com.yoanesber.spring.security.jwt_auth_postgresql.service.NetflixShowsService;
import com.yoanesber.spring.security.jwt_auth_postgresql.repository.NetflixShowsRepository;

/**
 * NetflixShowsServiceImpl is a service class that implements the NetflixShowsService interface.
 * It provides methods to manage Netflix shows in the database.
 * The @Service annotation indicates that this class is a Spring service component.
 */

@Service
public class NetflixShowsServiceImpl implements NetflixShowsService {
    
    private final NetflixShowsRepository netflixShowsRepository;

    public NetflixShowsServiceImpl(NetflixShowsRepository netflixShowsRepository) {
        this.netflixShowsRepository = netflixShowsRepository;
    }

    @Override
    @Transactional
    public NetflixShowsDTO createNetflixShows(NetflixShowsDTO netflixShowsDTO) {
        Assert.notNull(netflixShowsDTO, "NetflixShowsDTO must not be null");

        try {
            // Create NetflixShows object
            NetflixShows netflixShows = new NetflixShows();
            netflixShows.setShowType(EShowType.valueOf(netflixShowsDTO.getShowType()));
            netflixShows.setTitle(netflixShowsDTO.getTitle());
            netflixShows.setDirector(netflixShowsDTO.getDirector());
            netflixShows.setCastMembers(netflixShowsDTO.getCastMembers());
            netflixShows.setCountry(netflixShowsDTO.getCountry());
            netflixShows.setDateAdded(netflixShowsDTO.getDateAdded());
            netflixShows.setReleaseYear(netflixShowsDTO.getReleaseYear());
            netflixShows.setRating(netflixShowsDTO.getRating());
            netflixShows.setDurationInMinute(netflixShowsDTO.getDurationInMinute());
            netflixShows.setListedIn(netflixShowsDTO.getListedIn());
            netflixShows.setDescription(netflixShowsDTO.getDescription());

            // Save NetflixShows object & Return NetflixShowsDTO
            return new NetflixShowsDTO(netflixShowsRepository.save(netflixShows));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create NetflixShows: " + e.getMessage());
        }
    }

    @Override
    public List<NetflixShowsDTO> getAllNetflixShows() {
        try {
            // Get all NetflixShows
            List<NetflixShows> netflixShows = netflixShowsRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

            // Check if the list is empty
            if (netflixShows.isEmpty()) {
                return null;
            }

            // Convert NetflixShows to NetflixShowsDTO
            return netflixShows.stream().map(NetflixShowsDTO::new).toList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get all NetflixShows: " + e.getMessage());
        }
    }

    @Override
    public NetflixShowsDTO getNetflixShowsById(Long id) {
        Assert.notNull(id, "ID must not be null");

        try {
            // Get NetflixShows by ID
            NetflixShows netflixShows = netflixShowsRepository.findById(id)
                .orElse(null);

            // Check if the NetflixShows is null
            if (netflixShows == null) {
                return null;
            }

            // Return NetflixShowsDTO
            return new NetflixShowsDTO(netflixShows);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get NetflixShows by ID: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public NetflixShowsDTO updateNetflixShows(Long id, NetflixShowsDTO netflixShowsDTO) {
        Assert.notNull(netflixShowsDTO, "NetflixShowsDTO must not be null");
        Assert.notNull(id, "ID must not be null");

        try {
            // Get NetflixShows by ID
            NetflixShows netflixShows = netflixShowsRepository.findById(id)
                .orElse(null);

            // Check if the NetflixShows is null
            if (netflixShows == null) {
                return null;
            }

            // Update NetflixShows object
            netflixShows.setShowType(EShowType.valueOf(netflixShowsDTO.getShowType()));
            netflixShows.setTitle(netflixShowsDTO.getTitle());
            netflixShows.setDirector(netflixShowsDTO.getDirector());
            netflixShows.setCastMembers(netflixShowsDTO.getCastMembers());
            netflixShows.setCountry(netflixShowsDTO.getCountry());
            netflixShows.setDateAdded(netflixShowsDTO.getDateAdded());
            netflixShows.setReleaseYear(netflixShowsDTO.getReleaseYear());
            netflixShows.setRating(netflixShowsDTO.getRating());
            netflixShows.setDurationInMinute(netflixShowsDTO.getDurationInMinute());
            netflixShows.setListedIn(netflixShowsDTO.getListedIn());
            netflixShows.setDescription(netflixShowsDTO.getDescription());

            // Save NetflixShows object & Return NetflixShowsDTO
            return new NetflixShowsDTO(netflixShowsRepository.save(netflixShows));
        } catch (Exception e) {
            throw new RuntimeException("Failed to update NetflixShows: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Boolean deleteNetflixShows(Long id) {
        Assert.notNull(id, "ID must not be null");

        try {
            // Get NetflixShows by ID
            NetflixShows netflixShows = netflixShowsRepository.findById(id)
                .orElse(null);

            // Check if the NetflixShows is null
            if (netflixShows == null) {
                return false;
            }

            // Delete NetflixShows object
            netflixShowsRepository.delete(netflixShows);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete NetflixShows: " + e.getMessage());
        }
    }
}
