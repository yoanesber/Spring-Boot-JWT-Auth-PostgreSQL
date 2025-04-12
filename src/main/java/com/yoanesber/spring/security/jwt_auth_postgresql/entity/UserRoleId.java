package com.yoanesber.spring.security.jwt_auth_postgresql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * UserRoleId is an embedded ID class that represents the composite key for the UserRole entity.
 * The @Data annotation from Lombok generates getters, setters, equals, hashCode, and toString methods.
 * The @NoArgsConstructor and @AllArgsConstructor annotations are used to create constructors for the class.
 * The @Embeddable annotation indicates that this class can be embedded in another entity.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class UserRoleId {
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer roleId;
}
