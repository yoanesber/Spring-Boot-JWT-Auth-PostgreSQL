package com.yoanesber.spring.security.jwt_auth_postgresql.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JwtClaim is an entity class that represents the claims stored in a JWT (JSON Web Token).
 */

@AllArgsConstructor // Helps create DTO objects easily (useful when converting from entities).
@Data
@Getter
@NoArgsConstructor // Required for Jackson deserialization when receiving JSON requests.
@Setter
public class JwtClaim {
    private Long userId;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String userType;
    public List<String> roles = new ArrayList<>();

    public JwtClaim(User user) {
        this.userId = user.getId();
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.userType = user.getUserType().name();
        this.roles = user.getRoles().stream().map(Role::getName).toList();
    }

    @Override
    public String toString() {
        return "JwtClaim{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userType='" + userType + '\'' +
                ", roles=" + roles +
                '}';
    }
}