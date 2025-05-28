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
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * NetflixShows is an entity class that represents a Netflix show in the database.
 * The @Data annotation from Lombok generates getters, setters, equals, hashCode, and toString methods.
 * The @NoArgsConstructor and @AllArgsConstructor annotations are used to create constructors for the class.
 * The @Entity annotation indicates that this class is a JPA entity.
 * The @Table annotation specifies the name of the table in the database.
 */

@AllArgsConstructor // Helps create DTO objects easily (useful when converting from entities).
@Data
@Getter
@NoArgsConstructor // Required for Jackson deserialization when receiving JSON requests.
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

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "date_added", nullable = false)
    private Date dateAdded;

    @Column(name = "release_year", length = 4, nullable = false)
    private Integer releaseYear;

    @Column(name = "rating")
    private String rating;

    @Column(name = "duration")
    private String duration;

    @Column(name = "listed_in", columnDefinition = "TEXT")
    private String listedIn;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "updated_at", columnDefinition = "timestamp with time zone")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    @Column(name = "deleted_at", columnDefinition = "timestamp with time zone")
    private LocalDateTime deletedAt;
}
