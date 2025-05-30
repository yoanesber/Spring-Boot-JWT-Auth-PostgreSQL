package com.yoanesber.spring.security.jwt_auth_postgresql.entity;

import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * CustomUserDetails is a class that implements the UserDetails interface from Spring Security.
 * It represents the authenticated user and contains user-related information such as username, password, roles, etc.
 * This class is used to provide user details to the Spring Security framework during authentication and authorization processes.
 * The class wraps around the User entity and provides additional methods to access user information.
 */

public class CustomUserDetails implements UserDetails {
    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public static CustomUserDetails build(User user) {
        return new CustomUserDetails(user);
    }

    public Long getId() {
        return this.user.getId();
    }
    
    @Override
    public String getUsername() {
        return this.user.getUserName();
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    public String getEmail() {
        return this.user.getEmail();
    }

    public String getFirstName() {
        return this.user.getFirstName();
    }

    public String getLastName() {
        return this.user.getLastName();
    }

    @Override
    public boolean isEnabled() {
        return this.user.isEnabled();
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.user.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.user.isCredentialsNonExpired();
    }

    public EUserType getUserType() {
        return this.user.getUserType();
    }

    public Instant getLastLogin() {
        return this.user.getLastLogin();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "CustomUserDetails [id=" + user.getId() + ", username=" + user.getUserName() + ", email=" + user.getEmail()
                + ", firstName=" + user.getFirstName() + ", lastName=" + user.getLastName() + ", isEnabled="
                + user.isEnabled() + ", isAccountNonExpired=" + user.isAccountNonExpired() + ", isAccountNonLocked="
                + user.isAccountNonLocked() + ", isCredentialsNonExpired=" + user.isCredentialsNonExpired()
                + ", userType=" + user.getUserType().name() + ", lastLogin=" + user.getLastLogin() + "]";
    }
}
