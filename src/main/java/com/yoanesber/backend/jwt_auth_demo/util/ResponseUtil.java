package com.yoanesber.backend.jwt_auth_demo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.yoanesber.backend.jwt_auth_demo.dto.HttpResponseDTO;

/** 
 * ResponseUtil is a utility class that provides methods to build HTTP responses
 * in a consistent format. It helps in creating responses with various HTTP status codes,
 * messages, and data, and ensures that the response is in JSON format.
 */
@Component
public class ResponseUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void buildResponse(HttpServletRequest request,
        HttpServletResponse response, HttpStatus status, String message,
        String error, Object data) throws IOException {

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(response.getWriter(), 
            new HttpResponseDTO(
                message,
                error,
                request.getRequestURI(),
                status.value(),
                data
            ));
    }
    
    public static ResponseEntity<HttpResponseDTO> buildBadRequestResponse(HttpServletRequest request,
        String message, String error, Object data) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new HttpResponseDTO(
                message,
                error,
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                data
            ));
    }

    public static ResponseEntity<HttpResponseDTO> buildOkResponse(HttpServletRequest request,
        String message, Object data) {
        return ResponseEntity.ok(new HttpResponseDTO(
            message,
            null,
            request.getRequestURI(),
            HttpStatus.OK.value(),
            data
        ));
    }

    public static ResponseEntity<HttpResponseDTO> buildOkWithCookiesResponse(HttpServletRequest request,
        String message, Object data, String cookieName, String cookieValue) {
        return ResponseEntity.ok()
            .header(cookieName, cookieValue)
            .body(new HttpResponseDTO(
                message,
                null,
                request.getRequestURI(),
                HttpStatus.OK.value(),
                data
            ));
    }

    public static ResponseEntity<HttpResponseDTO> buildNotFoundResponse(HttpServletRequest request,
        String message, String error, Object data) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new HttpResponseDTO(
                message,
                error,
                request.getRequestURI(),
                HttpStatus.NOT_FOUND.value(),
                data
            ));
    }

    public static ResponseEntity<HttpResponseDTO> buildInternalServerErrorResponse(HttpServletRequest request,
        String message, String error, Object data) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new HttpResponseDTO(
                message,
                error,
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                data
            ));
    }

    public static ResponseEntity<HttpResponseDTO> buildCreatedResponse(HttpServletRequest request,
        String message, Object data) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new HttpResponseDTO(
                message,
                null,
                request.getRequestURI(),
                HttpStatus.CREATED.value(),
                data
            ));
    }

    public static ResponseEntity<HttpResponseDTO> buildUnauthorizedResponse(HttpServletRequest request,
        String message, String error, Object data) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new HttpResponseDTO(
                message,
                error,
                request.getRequestURI(),
                HttpStatus.UNAUTHORIZED.value(),
                data
            ));
    }

}
