package com.yoanesber.spring.security.jwt_auth_postgresql.handler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.CustomHttpResponse;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.CustomUserDetails;
import com.yoanesber.spring.security.jwt_auth_postgresql.service.CustomUserDetailsService;
import com.yoanesber.spring.security.jwt_auth_postgresql.service.JwtService;

/**
 * JwtAuthFilterHandler is a filter that handles JWT authentication for incoming requests.
 * It checks if the JWT token is present in the request header or cookies, validates it,
 * and sets the user in the security context if the token is valid.
 * If the token is invalid or missing, it sends an error response to the client.
 */

@Component
public class JwtAuthFilterHandler extends OncePerRequestFilter {
    private final JwtService jwtService;

    private final CustomUserDetailsService userDetailsServService;

    @Value("#{'${excluded-paths-for-authentication}'.split(',')}")
    private List<String> excludedPaths;

    public JwtAuthFilterHandler(JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsServService = customUserDetailsService;
    }

    // Check if the JWT token is valid and set the user in the security context
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Get the JWT token from the request header
        String jwtToken = jwtService.getJwtFromHeader(request);

        // Get the JWT token from the request cookies (if the token is not found in the header)
        if (jwtToken == null || jwtToken.isEmpty()) {
            jwtToken = jwtService.getJwtFromCookies(request);
        }

        try {
            // Check if the JWT token is not null and has a valid format
            if (jwtToken != null && !jwtToken.isEmpty()) {
                if (jwtService.validateToken(jwtToken)) {
                    // Get the username from the JWT token
                    String username = jwtService.getUserNameFromToken(jwtToken);

                    // Load the user by its username
                    CustomUserDetails userDetails = (CustomUserDetails) userDetailsServService.loadUserByUsername(username);
                    // UserDetails userDetails = userDetailsServService.loadUserByUsername(username);

                    // Set the user in the security context
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new RuntimeException("Invalid JWT token");
                }
            } else {
                throw new RuntimeException("JWT token is missing");
            }

            // Continue the filter chain
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthorized request", e.getMessage());
        }
    }

    // Send an error response to the client
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message, Object data) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // Create a custom HTTP response
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(new CustomHttpResponse(
            status.value(),
            message,
            data
        )));
    }

    // Skip the filter if the request is for the authentication endpoint or the excluded paths
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return excludedPaths.stream().anyMatch(path::startsWith);
    }
}