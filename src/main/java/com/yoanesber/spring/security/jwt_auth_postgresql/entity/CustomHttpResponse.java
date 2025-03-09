package com.yoanesber.spring.security.jwt_auth_postgresql.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.yoanesber.spring.security.jwt_auth_postgresql.config.serializer.InstantSerializer;

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