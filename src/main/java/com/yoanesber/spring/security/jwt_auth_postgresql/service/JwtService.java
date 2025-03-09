package com.yoanesber.spring.security.jwt_auth_postgresql.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import org.springframework.http.ResponseCookie;

public interface JwtService {
    // to get jwt string from request header based on jwt.header in application.properties
    String getJwtFromHeader(HttpServletRequest request);

    // to get jwt string from cookies based on jwt.cookieName in application.properties
    String getJwtFromCookies(HttpServletRequest request);

    // to generate jwt string based on jwt.secret in application.properties
    String generateJwtFromUsername(String username);

    // to generate and put jwt string into cookie based on jwt.cookieName in application.properties
    ResponseCookie generateJwtCookie(String username);

    // to remove jwt cookie
    ResponseCookie getCleanJwtCookie();

    // to get username from token (jwt string)
    String getUserNameFromToken(String token);

    // to get expiration date from token (jwt string)
    Date getExpirationDateFromToken(String token);

    // to validate token (jwt string)
    boolean validateToken(String authToken);
}
