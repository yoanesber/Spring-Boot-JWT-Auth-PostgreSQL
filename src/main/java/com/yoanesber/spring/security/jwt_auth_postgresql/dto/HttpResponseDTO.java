package com.yoanesber.spring.security.jwt_auth_postgresql.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yoanesber.spring.security.jwt_auth_postgresql.config.serializer.InstantSerializer;

/**
 * HttpResponseDTO is a Data Transfer Object (DTO) that represents the HTTP response structure.
 * This class is used to standardize the response format for API endpoints.
 * The @Data annotation from Lombok generates getters, setters, equals, hashCode, and toString methods.
 * The @NoArgsConstructor and @AllArgsConstructor annotations are used to create constructors for the class.
 */
@Data
@Getter
@Setter
@NoArgsConstructor // Required for Jackson deserialization when receiving JSON requests.
@AllArgsConstructor // Helps create DTO objects easily (useful when converting from entities).
public class HttpResponseDTO {
    private String message;
    private String error;
    private String path;
    private Integer status;
    private Object data;

    @JsonSerialize(using = InstantSerializer.class)
    private Instant timestamp;

    public HttpResponseDTO(String message, String error, String path, Integer status, Object data) {
        this.message = message;
        this.error = error;
        this.path = path;
        this.status = status;
        this.data = data;
        this.timestamp = Instant.now();
    }
}
