package com.yoanesber.spring.security.jwt_auth_postgresql.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.yoanesber.spring.security.jwt_auth_postgresql.config.serializer.InstantSerializer;

/**
 * CustomHttpResponse is an entity class that represents a custom HTTP response structure.
 * It is used to standardize the response format for API endpoints in the application.
 * This class is used to transfer data between the server and client in a consistent format.
 * The @Data annotation from Lombok generates getters, setters, equals, hashCode, and toString methods.
 * The @NoArgsConstructor and @AllArgsConstructor annotations are used to create constructors for the class.
 */

@Data
@Getter
@Setter
@NoArgsConstructor // Required for Jackson deserialization when receiving JSON requests.
@AllArgsConstructor // Helps create DTO objects easily (useful when converting from entities).
public class CustomHttpResponse {
    private Integer statusCode;

    @JsonSerialize(using = InstantSerializer.class)
    private Instant timestamp;

    private String message;
    private Object data;

    public CustomHttpResponse(Integer statusCode, String message, Object data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now();
    }
}