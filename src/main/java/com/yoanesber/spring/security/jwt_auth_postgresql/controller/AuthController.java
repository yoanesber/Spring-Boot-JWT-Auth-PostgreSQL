package com.yoanesber.spring.security.jwt_auth_postgresql.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yoanesber.spring.security.jwt_auth_postgresql.config.security.JwtConfig;
import com.yoanesber.spring.security.jwt_auth_postgresql.dto.HttpResponseDTO;
import com.yoanesber.spring.security.jwt_auth_postgresql.dto.LoginRequestDTO;
import com.yoanesber.spring.security.jwt_auth_postgresql.dto.LoginResponseDTO;
import com.yoanesber.spring.security.jwt_auth_postgresql.dto.TokenRefreshRequestDTO;
import com.yoanesber.spring.security.jwt_auth_postgresql.dto.TokenRefreshResponseDTO;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.CustomUserDetails;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.JwtClaim;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.RefreshToken;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.User;
import com.yoanesber.spring.security.jwt_auth_postgresql.service.RefreshTokenService;
import com.yoanesber.spring.security.jwt_auth_postgresql.service.UserService;
import com.yoanesber.spring.security.jwt_auth_postgresql.util.JwtUtil;
import com.yoanesber.spring.security.jwt_auth_postgresql.util.ResponseUtil;

/**
 * AuthController is a REST controller that handles authentication and authorization requests.
 * It provides endpoints for user login and token refresh.
 * The controller uses JWT (JSON Web Token) for authentication and authorization.
 * It also uses refresh tokens to obtain new access tokens without requiring the user to log in again.
 */

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    private static final String FAILED_TO_GENERATE_JWT_COOKIE = "Failed to generate JWT cookie";
    private static final String FAILED_TO_GENERATE_JWT_TOKEN = "Failed to generate JWT token";
    private static final String FAILED_TO_GENERATE_REFRESH_TOKEN = "Failed to generate refresh token";
    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    private static final String INVALID_REFRESH_TOKEN = "Invalid Refresh Token";
    private static final String INVALID_REQUEST = "Invalid Request";
    private static final String LOGIN_SUCCESS = "Login successful";
    private static final String REFRESH_TOKEN_SUCCESS = "Refresh token successful";

    public AuthController(AuthenticationManager authenticationManager, 
        RefreshTokenService refreshTokenService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpResponseDTO> login(@RequestBody LoginRequestDTO loginRequest, 
        HttpServletRequest request) {
        // Validate the login request
        if (loginRequest == null || loginRequest.getUsername() == null || 
            loginRequest.getPassword() == null) {
            // Return bad request response
            return ResponseUtil.buildBadRequestResponse(request, 
                INVALID_REQUEST, 
                "Username and password must not be null", 
                null);
        }

        Authentication authentication = null;

        try {
            // Authenticate user
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), 
                    loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseUtil.buildUnauthorizedResponse(request, 
                "Authentication Failed",
                "Invalid username or password", 
                null);
        } catch (DisabledException e) {
            return ResponseUtil.buildUnauthorizedResponse(request, 
                "Authentication Failed",
                e.getMessage(),
                null);
        } catch (AccountExpiredException e) {
            return ResponseUtil.buildUnauthorizedResponse(request, 
                "Authentication Failed",
                e.getMessage(),
                null);
        } catch (CredentialsExpiredException e) {
            return ResponseUtil.buildUnauthorizedResponse(request, 
                "Authentication Failed",
                e.getMessage(),
                null);
        } catch (LockedException e) {
            return ResponseUtil.buildUnauthorizedResponse(request, 
                "Authentication Failed",
                e.getMessage(),
                null);
        } catch (UsernameNotFoundException e) {
            return ResponseUtil.buildUnauthorizedResponse(request, 
                "Authentication Failed",
                "User not found with username: " + loginRequest.getUsername(), 
                null);
        } catch (Exception e) {
            return ResponseUtil.buildInternalServerErrorResponse(request, 
                INTERNAL_SERVER_ERROR, 
                "An error occurred while processing the request: " + e.getMessage(), 
                null);
        }

        try {
            // Set the authentication object in the security context. This will allow the user to access the protected resources.
            // Spring Security uses the SecurityContext to determine whether the current user is authenticated and authorized to access specific resources
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get the user details from the authentication object
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // Construct JWT claim from user details
            JwtClaim jwtClaim = new JwtClaim(new User(userDetails));

            // Generate JWT token
            String jwtToken = JwtUtil.generateJwtToken(jwtClaim);

            // Check if the JWT token is null or empty
            if (jwtToken == null || jwtToken.isEmpty()) {
                return ResponseUtil.buildInternalServerErrorResponse(request, 
                    FAILED_TO_GENERATE_JWT_TOKEN, 
                    "An error occurred while generating the JWT token", 
                    null);
            }

            // Generate refresh token
            RefreshToken jwtRefreshToken = refreshTokenService
                .createRefreshToken(userDetails.getId());

            // Check if the refresh token is null
            if (jwtRefreshToken == null) {
                return ResponseUtil.buildInternalServerErrorResponse(request, 
                    FAILED_TO_GENERATE_REFRESH_TOKEN, 
                    "An error occurred while generating the refresh token", 
                    null);
            }

            // Update the last login of the user
            userService.updateLastLogin(userDetails.getUsername());

            // Generate JWT cookie
            if (JwtConfig.getStaticCookieResponseEnabled()) {
                ResponseCookie jwtCookie = JwtUtil.generateJwtCookie(jwtClaim);

                // Check if the JWT cookie is null
                if (jwtCookie == null) {
                    return ResponseUtil.buildInternalServerErrorResponse(request, 
                        FAILED_TO_GENERATE_JWT_COOKIE, 
                        "An error occurred while generating the JWT cookie", 
                        null);
                }

                // Return ok response with cookies
                return ResponseUtil.buildOkWithCookiesResponse(request, 
                    LOGIN_SUCCESS, 
                    new LoginResponseDTO(
                        jwtToken, 
                        jwtRefreshToken.getId().getToken(), 
                        JwtUtil.getExpirationDateFromToken(jwtToken),
                        JwtConfig.getStaticTokenType()
                    ), "Set-Cookie", jwtCookie.toString());

            } else {
                // Return ok response without cookies
                return ResponseUtil.buildOkResponse(request, 
                    LOGIN_SUCCESS, 
                    new LoginResponseDTO(
                        jwtToken, 
                        jwtRefreshToken.getId().getToken(), 
                        JwtUtil.getExpirationDateFromToken(jwtToken),
                        JwtConfig.getStaticTokenType()
                    ));
            }
        } catch (Exception e) {
            return ResponseUtil.buildInternalServerErrorResponse(request, 
                INTERNAL_SERVER_ERROR, 
                "An error occurred while processing the request: " + e.getMessage(), 
                null);
        }
    }

    @PostMapping(value = "/refresh-token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpResponseDTO> refreshToken(@RequestBody TokenRefreshRequestDTO tokenRefreshRequest, 
        HttpServletRequest request) {
        // Validate the token refresh request
        if (tokenRefreshRequest == null || tokenRefreshRequest.getRefreshToken() == null) {
            return ResponseUtil.buildBadRequestResponse(request, 
                INVALID_REQUEST, 
                "Refresh token must not be null", 
                null);
        }

        try {
            // Find the refresh token
            RefreshToken jwtRefreshToken = refreshTokenService
                .findByToken(tokenRefreshRequest.getRefreshToken()).orElse(null);

            // Check if the refresh token is null
            if (jwtRefreshToken == null) {
                return ResponseUtil.buildBadRequestResponse(request, 
                    INVALID_REFRESH_TOKEN, 
                    "The provided refresh token is invalid or does not exist", 
                    null);
            }
            
            // Verify the expiration of the refresh token
            if (refreshTokenService.isTokenExpired(jwtRefreshToken)) {
                return ResponseUtil.buildBadRequestResponse(request, 
                    INVALID_REFRESH_TOKEN, 
                    "The provided refresh token is expired or invalid", 
                    null);
            }

            // Get the user details from the refresh token
            User userDetails = jwtRefreshToken.getUser();

            // Construct JWT claim from user details
            JwtClaim jwtClaim = new JwtClaim(userDetails);

            // Generate JWT token from the user details
            String jwtToken = JwtUtil.generateJwtToken(jwtClaim);

            // Check if the JWT token is null or empty
            if (jwtToken == null || jwtToken.isEmpty()) {
                return ResponseUtil.buildInternalServerErrorResponse(request, 
                    FAILED_TO_GENERATE_JWT_TOKEN, 
                    "An error occurred while generating the JWT token", 
                    null);
            }

            // Generate new refresh token (rotating refresh tokens)
            RefreshToken newJwtRefreshToken = refreshTokenService
                .createRefreshToken(userDetails.getId());

            // Check if the refresh token is null
            if (newJwtRefreshToken == null) {
                return ResponseUtil.buildInternalServerErrorResponse(request, 
                    FAILED_TO_GENERATE_REFRESH_TOKEN, 
                    "An error occurred while generating the refresh token", 
                    null);
            }

            // Generate JWT cookie
            if (JwtConfig.getStaticCookieResponseEnabled()) {
                ResponseCookie jwtCookie = JwtUtil.generateJwtCookie(jwtClaim);

                // Check if the JWT cookie is null
                if (jwtCookie == null) {
                    return ResponseUtil.buildInternalServerErrorResponse(request, 
                        FAILED_TO_GENERATE_JWT_COOKIE, 
                        "An error occurred while generating the JWT cookie", 
                        null);
                }

                // Return ok response with cookies
                return ResponseUtil.buildOkWithCookiesResponse(request, 
                    REFRESH_TOKEN_SUCCESS, 
                    new TokenRefreshResponseDTO(
                        jwtToken, 
                        newJwtRefreshToken.getId().getToken(), 
                        JwtUtil.getExpirationDateFromToken(jwtToken),
                        JwtConfig.getStaticTokenType()
                    ), "Set-Cookie", jwtCookie.toString());
            } else {
                // Return ok response without cookies
                return ResponseUtil.buildOkResponse(request, 
                    REFRESH_TOKEN_SUCCESS, 
                    new TokenRefreshResponseDTO(
                        jwtToken, 
                        newJwtRefreshToken.getId().getToken(), 
                        JwtUtil.getExpirationDateFromToken(jwtToken),
                        JwtConfig.getStaticTokenType()
                    ));
            }
        } catch (Exception e) {
            return ResponseUtil.buildInternalServerErrorResponse(request, 
                INTERNAL_SERVER_ERROR, 
                "An error occurred while processing the request: " + e.getMessage(), 
                null);
        }
    }
}
