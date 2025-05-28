package com.yoanesber.spring.security.jwt_auth_postgresql.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.yoanesber.spring.security.jwt_auth_postgresql.util.ResponseUtil;

/**
 * CustomAccessDeniedHandler is a class that implements the AccessDeniedHandler interface.
 * It handles access denied exceptions in a custom way by sending a JSON response with the error details.
 * This class is used to provide a consistent error response format for access denied errors in the application.
 */

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    // Handle the access denied exception and send the response
    @Override
    public void handle(HttpServletRequest request, 
        HttpServletResponse response, 
        AccessDeniedException ex) throws IOException, ServletException {

        // Build the error response using ResponseUtil
        ResponseUtil.buildResponse(request, response, HttpStatus.FORBIDDEN, 
            "Access Denied", 
            "You do not have permission to access this resource.", 
            null);
    }
}