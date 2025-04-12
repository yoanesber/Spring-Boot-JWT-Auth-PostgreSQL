package com.yoanesber.spring.security.jwt_auth_postgresql.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TokenRefreshRequestDTO is a Data Transfer Object (DTO) that represents the request payload for refreshing a token.
 * This class is used to transfer data between the client and server during the token refresh process.
 * The @Data annotation from Lombok generates getters, setters, equals, hashCode, and toString methods.
 * The @NoArgsConstructor and @AllArgsConstructor annotations are used to create constructors for the class.
 */

@Data
@Getter
@Setter
@NoArgsConstructor // Required for Jackson deserialization when receiving JSON requests.
@AllArgsConstructor // Helps create DTO objects easily (useful when converting from entities).
public class TokenRefreshRequestDTO {
    private String refreshToken;
}