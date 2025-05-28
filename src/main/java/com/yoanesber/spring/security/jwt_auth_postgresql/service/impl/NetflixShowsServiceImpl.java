package com.yoanesber.spring.security.jwt_auth_postgresql.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.yoanesber.spring.security.jwt_auth_postgresql.dto.NetflixShowsDTO;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.EShowType;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.NetflixShows;
import com.yoanesber.spring.security.jwt_auth_postgresql.mapper.NetflixShowsMapper;
import com.yoanesber.spring.security.jwt_auth_postgresql.service.NetflixShowsService;
import com.yoanesber.spring.security.jwt_auth_postgresql.util.SecurityUtil;
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
        netflixShows.setDuration(netflixShowsDTO.getDuration());
        netflixShows.setListedIn(netflixShowsDTO.getListedIn());
        netflixShows.setDescription(netflixShowsDTO.getDescription());
        netflixShows.setCreatedBy(SecurityUtil.getCurrentUserId());
        netflixShows.setCreatedAt(LocalDateTime.now());

        // Save NetflixShows object & Return NetflixShowsDTO
        return NetflixShowsMapper.toDTO(
            netflixShowsRepository.save(netflixShows)
        );
    }

    @Override
    public List<NetflixShowsDTO> getAllNetflixShows() {
        // Get all NetflixShows
        List<NetflixShows> netflixShows = netflixShowsRepository
            .findAll(Sort.by(Sort.Direction.ASC, "id"));

        // Check if the list is empty
        if (netflixShows.isEmpty()) {
            return null;
        }

        // Convert NetflixShows to NetflixShowsDTO
        return netflixShows.stream().map(
            NetflixShowsMapper::toDTO
        ).toList();
    }

    @Override
    public NetflixShowsDTO getNetflixShowsById(Long id) {
        Assert.notNull(id, "ID must not be null");

        // Get NetflixShows by ID
        NetflixShows netflixShows = netflixShowsRepository.findById(id)
            .orElse(null);

        // Check if the NetflixShows is null
        if (netflixShows == null) {
            return null;
        }

        // Return NetflixShowsDTO
        return NetflixShowsMapper.toDTO(netflixShows);
    }

    @Override
    @Transactional
    public NetflixShowsDTO updateNetflixShows(Long id, NetflixShowsDTO netflixShowsDTO) {
        Assert.notNull(netflixShowsDTO, "NetflixShowsDTO must not be null");
        Assert.notNull(id, "ID must not be null");

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
        netflixShows.setDuration(netflixShowsDTO.getDuration());
        netflixShows.setListedIn(netflixShowsDTO.getListedIn());
        netflixShows.setDescription(netflixShowsDTO.getDescription());
        netflixShows.setUpdatedBy(SecurityUtil.getCurrentUserId());
        netflixShows.setUpdatedAt(LocalDateTime.now());

        // Save NetflixShows object & Return NetflixShowsDTO
        return NetflixShowsMapper.toDTO(
            netflixShowsRepository.save(netflixShows)
        );
    }

    @Override
    @Transactional
    public Boolean deleteNetflixShows(Long id) {
        Assert.notNull(id, "ID must not be null");

        // Get NetflixShows by ID
        NetflixShows netflixShows = netflixShowsRepository.findById(id)
            .orElse(null);

        // Check if the NetflixShows is null
        if (netflixShows == null) {
            return false;
        }

        // Set deleted fields
        netflixShows.setDeleted(true);
        netflixShows.setDeletedAt(LocalDateTime.now());
        netflixShows.setDeletedBy(SecurityUtil.getCurrentUserId());

        // Delete NetflixShows object
        netflixShowsRepository.save(netflixShows);
        return true;
    }
}
