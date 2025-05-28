package com.yoanesber.spring.security.jwt_auth_postgresql.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * LoginResponseDTO is a Data Transfer Object (DTO) that represents the login response payload.
 * This class is used to transfer data between the server and client after a successful login.
 * The @Data annotation from Lombok generates getters, setters, equals, hashCode, and toString methods.
 * The @NoArgsConstructor and @AllArgsConstructor annotations are used to create constructors for the class.
 * 
 * The expirationDate field is formatted using the @JsonFormat annotation to ensure that it is serialized and deserialized correctly.
 * The pattern "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" is used to represent the date in ISO 8601 format.
 */

@Data
@Getter
@Setter
@NoArgsConstructor // Required for Jackson deserialization when receiving JSON requests.
@AllArgsConstructor // Helps create DTO objects easily (useful when converting from entities).
public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Date expirationDate;
    private String tokenType;
}
