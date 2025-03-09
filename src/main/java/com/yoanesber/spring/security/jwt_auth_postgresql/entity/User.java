package com.yoanesber.spring.security.jwt_auth_postgresql.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isEnabled = false;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isAccountNonExpired = false;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isAccountNonLocked = false;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isCredentialsNonExpired = false;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted = false;

    private Instant accountExpirationDate;
    private Instant credentialsExpirationDate;
    private Instant lastLogin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EUserType userType;

    @Column(nullable = false, length = 20)
    private String createdBy;

    @Column(nullable = false, columnDefinition = "timestamp with time zone default now()")
    private Instant createdDate = Instant.now();

    @Column(nullable = false, length = 20)
    private String updatedBy;

    @Column(nullable = false, columnDefinition = "timestamp with time zone default now()")
    private Instant updatedDate = Instant.now();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    // mappedBy is the name of the field in the other entity that maps this relationship
    // cascade = CascadeType.ALL means that if an User is deleted, all related UserRole will also be deleted
    // orphanRemoval = true means that if a UserRole is removed from the list, it will be deleted from the database. 
    // Because ON DELETE CASCADE is used, orphan removal should be enabled.
    private List<UserRole> userRoles = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = false)
    // mappedBy is the name of the field in the other entity that maps this relationship
    // cascade = CascadeType.ALL means that if an User is deleted, the related RefreshToken will also be deleted
    // orphanRemoval = false Because ON DELETE NO ACTION is used, orphan removal should not be enabled
    private RefreshToken refreshToken;

    @Override
    public String toString() {
        return "User [id=" + id + ", userName=" + userName + ", email=" + email + ", firstName=" + firstName + ", lastName=" + lastName + "]";
    }
}
