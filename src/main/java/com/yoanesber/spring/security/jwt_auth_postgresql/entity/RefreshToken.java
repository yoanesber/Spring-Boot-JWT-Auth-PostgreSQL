package com.yoanesber.spring.security.jwt_auth_postgresql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RefreshToken is an entity class that represents a refresh token in the database.
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
@Table(name = "refresh_token")
public class RefreshToken {
    @EmbeddedId
    private RefreshTokenId id;

    @Column(nullable = false)
    private Instant expiryDate;
    
    @OneToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String toString() {
        return "RefreshToken [id=" + id + ", expiryDate=" + expiryDate + "]";
    }
}
