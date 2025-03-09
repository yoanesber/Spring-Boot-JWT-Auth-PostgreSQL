package com.yoanesber.spring.security.jwt_auth_postgresql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "netflix_shows")
public class NetflixShows {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 7, nullable = false)
    private EShowType showType;

    @Column(name = "title", columnDefinition = "TEXT", nullable = false)
    private String title;

    @Column(name = "director", columnDefinition = "TEXT")
    private String director;

    @Column(name = "cast_members", columnDefinition = "TEXT")
    private String castMembers;

    @Column(name = "country", length = 60, nullable = false)
    private String country;

    @Column(name = "date_added", nullable = false)
    private Date dateAdded;

    @Column(name = "release_year", length = 4, nullable = false)
    private Integer releaseYear;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "duration_in_minute")
    private Integer durationInMinute;

    @Column(name = "listed_in", columnDefinition = "TEXT")
    private String listedIn;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
