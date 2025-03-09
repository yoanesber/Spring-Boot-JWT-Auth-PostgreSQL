package com.yoanesber.spring.security.jwt_auth_postgresql.dto;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.yoanesber.spring.security.jwt_auth_postgresql.entity.NetflixShows;

@Data
@Getter
@Setter
@NoArgsConstructor // Required for Jackson deserialization when receiving JSON requests.
@AllArgsConstructor // Helps create DTO objects easily (useful when converting from entities).
public class NetflixShowsDTO {
    private Long id;
    private String showType;
    private String title;
    private String director;
    private String castMembers;
    private String country;
    private Date dateAdded;
    private Integer releaseYear;
    private Integer rating;
    private Integer durationInMinute;
    private String listedIn;
    private String description;

    public NetflixShowsDTO(NetflixShows netflixShows) {
        this.id = netflixShows.getId();
        this.showType = netflixShows.getShowType().name();
        this.title = netflixShows.getTitle();
        this.director = netflixShows.getDirector();
        this.castMembers = netflixShows.getCastMembers();
        this.country = netflixShows.getCountry();
        this.dateAdded = netflixShows.getDateAdded();
        this.releaseYear = netflixShows.getReleaseYear();
        this.rating = netflixShows.getRating();
        this.durationInMinute = netflixShows.getDurationInMinute();
        this.listedIn = netflixShows.getListedIn();
        this.description = netflixShows.getDescription();
    }
}
