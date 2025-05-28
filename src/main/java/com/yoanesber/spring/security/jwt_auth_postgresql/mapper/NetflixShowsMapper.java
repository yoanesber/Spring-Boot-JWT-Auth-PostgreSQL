package com.yoanesber.spring.security.jwt_auth_postgresql.mapper;

import com.yoanesber.spring.security.jwt_auth_postgresql.dto.NetflixShowsDTO;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.EShowType;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.NetflixShows;

/**
 * NetflixShowsMapper is a utility class that provides methods to convert between
 * NetflixShows entity and NetflixShowsDTO.
 * It contains static methods to convert from entity to DTO and vice versa.
 */
public class NetflixShowsMapper {
    /**
     * Converts a NetflixShows entity to a NetflixShowsDTO.
     *
     * @param netflixShows the NetflixShows entity to convert
     * @return the converted NetflixShowsDTO
     */
    public static NetflixShowsDTO toDTO(NetflixShows netflixShows) {
        if (netflixShows == null) {
            return null;
        }

        return new NetflixShowsDTO(
                netflixShows.getId(),
                netflixShows.getShowType().name(),
                netflixShows.getTitle(),
                netflixShows.getDirector(),
                netflixShows.getCastMembers(),
                netflixShows.getCountry(),
                netflixShows.getDateAdded(),
                netflixShows.getReleaseYear(),
                netflixShows.getRating(),
                netflixShows.getDuration(),
                netflixShows.getListedIn(),
                netflixShows.getDescription()
        );
    }

    /**
     * Converts a NetflixShowsDTO to a NetflixShows entity.
     *
     * @param netflixShowsDTO the NetflixShowsDTO to convert
     * @return the converted NetflixShows entity
     */
    public static NetflixShows toEntity(NetflixShowsDTO netflixShowsDTO) {
        if (netflixShowsDTO == null) {
            return null;
        }

        NetflixShows netflixShows = new NetflixShows();
        netflixShows.setId(netflixShowsDTO.getId());
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

        return netflixShows;
    }
}
