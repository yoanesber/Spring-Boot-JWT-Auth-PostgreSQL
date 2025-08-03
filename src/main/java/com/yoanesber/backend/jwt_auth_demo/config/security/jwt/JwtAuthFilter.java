package com.yoanesber.backend.jwt_auth_demo.config.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.yoanesber.backend.jwt_auth_demo.entity.CustomUserDetails;
import com.yoanesber.backend.jwt_auth_demo.service.CustomUserDetailsService;
import com.yoanesber.backend.jwt_auth_demo.util.JwtUtil;
import com.yoanesber.backend.jwt_auth_demo.util.ResponseUtil;

/**
 * JwtAuthFilterHandler is a filter that handles JWT authentication for incoming requests.
 * It checks if the JWT token is present in the request header or cookies, validates it,
 * and sets the user in the security context if the token is valid.
 * If the token is invalid or missing, it sends an error response to the client.
 */

@Configuration
public class JwtAuthFilter extends OncePerRequestFilter {
    private final CustomUserDetailsService userDetailsServService;

    @Value("#{'${excluded-paths-for-authentication}'.split(',')}")
    private List<String> excludedPaths;

    public JwtAuthFilter(CustomUserDetailsService customUserDetailsService) {
        this.userDetailsServService = customUserDetailsService;
    }

    // Check if the JWT token is valid and set the user in the security context
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
        FilterChain filterChain) throws ServletException, IOException {

        // Check if the request method is POST and the Content-Type is not application/json
        // If the Content-Type is not application/json, send an error response
        String contentType = request.getContentType();
        if ((request.getMethod().equals("POST") || request.getMethod().equals("PUT")) && 
            (contentType == null || !contentType.equals(MediaType.APPLICATION_JSON_VALUE))) {
            ResponseUtil.buildResponse(request, response, HttpStatus.UNSUPPORTED_MEDIA_TYPE, 
                "Unsupported Media Type", "Content-Type must be application/json", null);
            return;
        }

        // If the request is for the authentication endpoint, skip the filter
        // This allows the authentication endpoint to be accessed without a JWT token
        // Also skip the filter for any paths that are excluded from authentication
        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Get the JWT token from the request header
            String jwtToken = JwtUtil.getJwtFromHeader(request);

            // Get the JWT token from the request cookies (if the token is not found in the header)
            if (jwtToken == null || jwtToken.isEmpty()) {
                jwtToken = JwtUtil.getJwtFromCookies(request);
            }

            // If the JWT token is still null or empty, send an error response
            // This means the request is unauthorized
            if (jwtToken == null || jwtToken.isEmpty()) {
                ResponseUtil.buildResponse(request, response, HttpStatus.UNAUTHORIZED, "Unauthorized request", "JWT token is missing or invalid", null);
                return;
            }
            
            // Check if the JWT token is not null and has a valid format
            if (jwtToken != null && !jwtToken.isEmpty()) {
                if (JwtUtil.validateToken(jwtToken)) {
                    // Get the username from the JWT token
                    String username = JwtUtil.getUserNameFromToken(jwtToken);

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
        } catch (ExpiredJwtException e) {
            ResponseUtil.buildResponse(request, response, HttpStatus.UNAUTHORIZED, "Unauthorized request", "Token has expired", null);
            return;
        } catch (SignatureException e) {
            ResponseUtil.buildResponse(request, response, HttpStatus.UNAUTHORIZED, "Unauthorized request", "Invalid token signature", null);
            return;
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            ResponseUtil.buildResponse(request, response, HttpStatus.UNAUTHORIZED, "Unauthorized request", "Malformed or unsupported JWT token", null);
            return;
        } catch (Exception e) {
            ResponseUtil.buildResponse(request, response, HttpStatus.UNAUTHORIZED, "Unauthorized request", e.getMessage(), null);
            return;
        }
    }

    // Skip the filter if the request is for the authentication endpoint or the excluded paths
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return excludedPaths.stream().anyMatch(path::startsWith);
    }
}