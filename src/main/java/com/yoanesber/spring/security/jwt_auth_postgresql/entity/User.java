package com.yoanesber.spring.security.jwt_auth_postgresql.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User is an entity class that represents a user in the database.
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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, length = 20, unique = true)
    private String userName;

    @Column(nullable = false, length = 150)
    private String password;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "firstname", nullable = false, length = 20)
    private String firstName;

    @Column(name = "lastname", length = 20)
    private String lastName;

    @Column(name = "is_enabled", nullable = false, columnDefinition = "boolean default true")
    private boolean isEnabled;

    @Column(name = "is_account_non_expired", nullable = false, columnDefinition = "boolean default true")
    private boolean isAccountNonExpired;

    @Column(name = "is_account_non_locked", nullable = false, columnDefinition = "boolean default true")
    private boolean isAccountNonLocked;

    @Column(name = "is_credentials_non_expired", nullable = false, columnDefinition = "boolean default true")
    private boolean isCredentialsNonExpired;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted;

    @Column(name = "account_expiration_date", columnDefinition = "timestamp with time zone")
    private Instant accountExpirationDate;

    @Column(name = "credentials_expiration_date", columnDefinition = "timestamp with time zone")
    private Instant credentialsExpirationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 20)
    private EUserType userType;

    @Column(name = "last_login", columnDefinition = "timestamp with time zone")
    private Instant lastLogin;

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

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "user_roles", // nama tabel join
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    public List<Role> roles = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = false)
    // mappedBy is the name of the field in the other entity that maps this relationship
    // cascade = CascadeType.ALL means that if an User is deleted, the related RefreshToken will also be deleted
    // orphanRemoval = false Because ON DELETE NO ACTION is used, orphan removal should not be enabled
    private RefreshToken refreshToken;

    // Mapping constructor to create a User entity from CustomUserDetails
    public User(CustomUserDetails userDetails) {
        this.id = userDetails.getId();
        this.userName = userDetails.getUsername();
        this.password = userDetails.getPassword();
        this.email = userDetails.getEmail();
        this.firstName = userDetails.getFirstName();
        this.lastName = userDetails.getLastName();
        this.isEnabled = userDetails.isEnabled();
        this.isAccountNonExpired = userDetails.isAccountNonExpired();
        this.isAccountNonLocked = userDetails.isAccountNonLocked();
        this.isCredentialsNonExpired = userDetails.isCredentialsNonExpired();
        this.userType = userDetails.getUserType();
        this.lastLogin = userDetails.getLastLogin();
        this.roles = userDetails.getAuthorities().stream()
                .map(authority -> new Role(authority.getAuthority()))
                .toList();
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", userName=" + userName + ", password=" + password + ", email=" + email
                + ", firstName=" + firstName + ", lastName=" + lastName + ", isEnabled=" + isEnabled
                + ", isAccountNonExpired=" + isAccountNonExpired + ", isAccountNonLocked=" + isAccountNonLocked
                + ", isCredentialsNonExpired=" + isCredentialsNonExpired + ", isDeleted=" + isDeleted
                + ", accountExpirationDate=" + accountExpirationDate + ", credentialsExpirationDate="
                + credentialsExpirationDate + ", lastLogin=" + lastLogin + ", userType=" + userType
                + ", createdBy=" + createdBy + ", createdAt=" + createdAt + ", updatedBy=" + updatedBy
                + ", updatedAt=" + updatedAt + ", deletedBy=" + deletedBy + ", deletedAt=" + deletedAt +
                ", roles=" + roles + "]";
    }
}
