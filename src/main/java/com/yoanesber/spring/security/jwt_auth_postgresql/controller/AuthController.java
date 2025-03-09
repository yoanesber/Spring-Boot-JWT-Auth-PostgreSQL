package com.yoanesber.spring.security.jwt_auth_postgresql.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yoanesber.spring.security.jwt_auth_postgresql.dto.LoginRequestDTO;
import com.yoanesber.spring.security.jwt_auth_postgresql.dto.LoginResponseDTO;
import com.yoanesber.spring.security.jwt_auth_postgresql.dto.TokenRefreshRequestDTO;
import com.yoanesber.spring.security.jwt_auth_postgresql.dto.TokenRefreshResponseDTO;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.CustomHttpResponse;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.CustomUserDetails;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.RefreshToken;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.User;
import com.yoanesber.spring.security.jwt_auth_postgresql.service.JwtService;
import com.yoanesber.spring.security.jwt_auth_postgresql.service.RefreshTokenService;
import com.yoanesber.spring.security.jwt_auth_postgresql.service.UserService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final UserService userService;

    @Value("${jwt.prefix}")
    private String tokenType;

    @Value("${jwt.tokenName}")
    private String jwtTokenName;

    @Value("${jwt.refreshTokenName}")
    private String jwtRefreshTokenName;

    @Value("${jwt.cookieResponseEnabled}")
    private boolean cookieResponseEnabled;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, RefreshTokenService refreshTokenService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Authentication authentication = null;

        try {
            // Authenticate user
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(new CustomHttpResponse(HttpStatus.BAD_REQUEST.value(), 
                "Invalid username or password: " + e.getMessage(), null));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomHttpResponse(HttpStatus.NOT_FOUND.value(), 
                    "User not found: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new CustomHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                e.getMessage(), null));
        }

        // Set the authentication object in the security context. This will allow the user to access the protected resources.
        // Spring Security uses the SecurityContext to determine whether the current user is authenticated and authorized to access specific resources
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get the user details from the authentication object
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Generate JWT token
        String jwtToken = jwtService.generateJwtFromUsername(userDetails.getUsername());

        // Check if the JWT token is null or empty
        if (jwtToken == null || jwtToken.isEmpty()) {
            return ResponseEntity.internalServerError().body(new CustomHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to generate JWT token", null));
        }

        // Generate refresh token
        RefreshToken jwtRefreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        // Check if the refresh token is null
        if (jwtRefreshToken == null) {
            return ResponseEntity.internalServerError().body(new CustomHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to generate refresh token", null));
        }

        // Update the last login of the user
        userService.updateLastLogin(userDetails.getUsername());

        // Generate JWT cookie
        if (cookieResponseEnabled) {
            ResponseCookie jwtCookie = jwtService.generateJwtCookie(userDetails.getUsername());

            // Check if the JWT cookie is null
            if (jwtCookie == null) {
                return ResponseEntity.internalServerError().body(new CustomHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Failed to generate JWT cookie", null));
            }

            // Return the response
            return ResponseEntity.ok()
            .header("Set-Cookie", jwtCookie.toString())
            .body(new CustomHttpResponse(HttpStatus.OK.value(), 
                "Login successful", new LoginResponseDTO(
                    jwtToken, 
                    jwtRefreshToken.getId().getToken(), 
                    jwtService.getExpirationDateFromToken(jwtToken),
                    tokenType
                )));
        } else {
            // Return the response
            return ResponseEntity.ok()
            .body(new CustomHttpResponse(HttpStatus.OK.value(), 
                "Login successful", new LoginResponseDTO(
                    jwtToken, 
                    jwtRefreshToken.getId().getToken(), 
                    jwtService.getExpirationDateFromToken(jwtToken),
                    tokenType
                )));
        }
    }

    @PostMapping(value = "/refresh-token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> refreshToken(@RequestBody TokenRefreshRequestDTO tokenRefreshRequestDTO) {
        // Find the refresh token
        RefreshToken jwtRefreshToken = refreshTokenService.findByToken(tokenRefreshRequestDTO.getRefreshToken()).orElse(null);

        // Check if the refresh token is null
        if (jwtRefreshToken == null) {
            return ResponseEntity.badRequest().body(new CustomHttpResponse(HttpStatus.BAD_REQUEST.value(), 
                "Invalid refresh token", null));
        }

        try {
            // Verify the expiration of the refresh token
            jwtRefreshToken = refreshTokenService.verifyExpiration(jwtRefreshToken);

            // Check if the refresh token is null
            if (jwtRefreshToken == null) {
                return ResponseEntity.badRequest().body(new CustomHttpResponse(HttpStatus.BAD_REQUEST.value(), 
                    "Invalid refresh token", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CustomHttpResponse(HttpStatus.UNAUTHORIZED.value(), 
                    e.getMessage(), null));
        }

        // Get the user details from the refresh token
        User userDetails = jwtRefreshToken.getUser();

        // Generate JWT token from the user details
        String jwtToken = jwtService.generateJwtFromUsername(userDetails.getUserName());

        // Check if the JWT token is null or empty
        if (jwtToken == null || jwtToken.isEmpty()) {
            return ResponseEntity.internalServerError().body(new CustomHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to generate JWT token", null));
        }

        // Generate new refresh token (rotating refresh tokens)
        RefreshToken newJwtRefreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        // Check if the refresh token is null
        if (newJwtRefreshToken == null) {
            return ResponseEntity.internalServerError().body(new CustomHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to generate refresh token", null));
        }

        // Generate JWT cookie
        if (cookieResponseEnabled) {
            ResponseCookie jwtCookie = jwtService.generateJwtCookie(userDetails.getUserName());

            // Check if the JWT cookie is null
            if (jwtCookie == null) {
                return ResponseEntity.internalServerError().body(new CustomHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Failed to generate JWT cookie", null));
            }

            // Return the response
            return ResponseEntity.ok()
            .header("Set-Cookie", jwtCookie.toString())
            .body(new CustomHttpResponse(HttpStatus.OK.value(), 
                "Refresh token successful", new TokenRefreshResponseDTO(
                    jwtToken, 
                    newJwtRefreshToken.getId().getToken(), 
                    jwtService.getExpirationDateFromToken(jwtToken),
                    tokenType
                )));
        } else {
            // Return the response
            return ResponseEntity.ok()
            .body(new CustomHttpResponse(HttpStatus.OK.value(), 
                "Refresh token successful", new TokenRefreshResponseDTO(
                    jwtToken, 
                    newJwtRefreshToken.getId().getToken(), 
                    jwtService.getExpirationDateFromToken(jwtToken),
                    tokenType
                )));
        }
    }
}
