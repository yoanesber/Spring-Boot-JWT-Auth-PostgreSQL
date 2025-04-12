package com.yoanesber.spring.security.jwt_auth_postgresql.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.yoanesber.spring.security.jwt_auth_postgresql.entity.CustomHttpResponse;

/**
 * CustomAuthExceptionHandler is a class that implements the AuthenticationEntryPoint interface.
 * It handles authentication exceptions in a custom way by sending a JSON response with the error details.
 * This class is used to provide a consistent error response format for authentication errors in the application.
 */

@Component
public class CustomAuthExceptionHandler implements AuthenticationEntryPoint {

    // Handle the authentication exception and send the response
    @Override
    public void commence(HttpServletRequest request, 
        HttpServletResponse response, 
        AuthenticationException authException) throws IOException, ServletException {

        // Get the error status code and message
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        // Set the default status code and message
        if (statusCode == null) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        
        // Set the default message based on the status code
        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            message = "The requested resource was not found";
        } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
            message = "You don't have permission to access this resource";
        } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
            message = "The request was invalid or cannot be served";
        } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
            message = "You need to authenticate to access this resource";
        } else {
            message = (message != null) ? message : "Unexpected error occurred";
        }

        // Set the response content type and status
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(statusCode);
        response.setCharacterEncoding("UTF-8");

        // Write the response as JSON
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), new CustomHttpResponse(
            statusCode,
            message,
            null
        ));
    }
}