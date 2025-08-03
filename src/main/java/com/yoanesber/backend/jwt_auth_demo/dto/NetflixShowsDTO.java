package com.yoanesber.backend.jwt_auth_demo.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * NetflixShowsDTO is a Data Transfer Object (DTO) that represents the Netflix shows data.
 * releaseYear, rating, durationInMinute, listedIn, and description.
 * This class is used to transfer data between the server and client.
 * The @Data annotation from Lombok generates getters, setters, equals, hashCode, and toString methods.
 * The @NoArgsConstructor and @AllArgsConstructor annotations are used to create constructors for the class.
 */

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
    private String rating;
    private String duration;
    private String listedIn;
    private String description;
}
