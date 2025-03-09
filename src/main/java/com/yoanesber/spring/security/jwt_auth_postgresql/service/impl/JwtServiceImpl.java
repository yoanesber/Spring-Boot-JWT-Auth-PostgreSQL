package com.yoanesber.spring.security.jwt_auth_postgresql.service.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.yoanesber.spring.security.jwt_auth_postgresql.service.JwtService;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.header}")
    private String jwtHeader;

    @Value("${jwt.prefix}")
    private String jwtPrefix;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private Long jwtExpirationMs;

    @Value("${jwt.cookieName}")
    private String jwtCookieName;

    @Value("${jwt.cookiePath}")
    private String jwtCookiePath;

    @Value("${jwt.cookieMaxAgeMs}")
    private Long jwtCookieMaxAgeMs;

    @Value("${jwt.cookieSecure}")
    private boolean jwtCookieSecure;

    @Value("${jwt.cookieHttpOnly}")
    private boolean jwtCookieHttpOnly;

    @Value("${jwt.cookieSameSite}")
    private String jwtCookieSameSite;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public JwtServiceImpl() {}

    @Override
    public String getJwtFromHeader(HttpServletRequest request) {
        Assert.notNull(request, "Request must not be null");
        
        try {
            // Get the Authorization value from the request header based on the header name
            String headerAuth = request.getHeader(jwtHeader);

            // Check if the Authorization value is not null and starts with the JWT prefix
            if (headerAuth != null && headerAuth.startsWith(jwtPrefix))
                return headerAuth.substring(jwtPrefix.length()).trim();

            return "";
        } catch (Exception e) {
            logger.error("Failed to get JWT token from header: " + e.getMessage());
            return "";
        }
    }

    @Override
    public String getJwtFromCookies(HttpServletRequest request) {
        Assert.notNull(request, "Request must not be null");
        
        try {
            // Get the JWT token from the request cookies based on the cookie name
            return request.getCookies() != null ? 
                java.util.Arrays.stream(request.getCookies())
                    .filter(cookie -> jwtCookieName.equals(cookie.getName()))
                    .map(cookie -> cookie.getValue())
                    .findFirst()
                    .orElse("") : "";
        } catch (Exception e) {
            logger.error("Failed to get JWT token from cookies: " + e.getMessage());
            return "";
        }
    }

    private SecretKey key() {
        // Generate the HMAC SHA key from the JWT secret
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateJwtFromUsername(String username) {
        Assert.notNull(username, "Username must not be null");

        try {
            // Generate the JWT token based on the username
            return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
        } catch (Exception e) {
            logger.error("Failed to generate JWT token from username: " + e.getMessage());
            return "";
        }
    }

    @Override
    public ResponseCookie generateJwtCookie(String username) {
        Assert.notNull(username, "Username must not be null");

        try {
            // Generate the JWT token from the user principal
            String jwtToken = generateJwtFromUsername(username);

            // Generate the JWT cookie based on the JWT token
            return ResponseCookie.from(jwtCookieName, jwtToken)
                .path(jwtCookiePath)
                .maxAge(jwtCookieMaxAgeMs)
                .secure(jwtCookieSecure)
                .httpOnly(jwtCookieHttpOnly)
                .sameSite(jwtCookieSameSite)
                .build();
        } catch (Exception e) {
            logger.error("Failed to generate JWT cookie: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseCookie getCleanJwtCookie() {
        try {
            // Generate the clean JWT cookie (remove the JWT token)
            return ResponseCookie.from(jwtCookieName, "")
                .path(jwtCookiePath)
                .maxAge(0)
                .secure(jwtCookieSecure)
                .httpOnly(jwtCookieHttpOnly)
                .sameSite(jwtCookieSameSite)
                .build();
        } catch (Exception e) {
            logger.error("Failed to clean JWT cookie: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String getUserNameFromToken(String jwtToken) {
        Assert.notNull(jwtToken, "JWT token must not be null");

        try {
            // Get the username from the JWT token based on the key
            return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody()
                .getSubject();
        } catch (Exception e) {
            logger.error("Failed to get username from JWT token: " + e.getMessage());
            return "";
        }
    }

    @Override
    public Date getExpirationDateFromToken(String token) {
        Assert.notNull(token, "Token must not be null");

        try {
            // Get the expiration date from the JWT token based on the key
            return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        } catch (Exception e) {
            logger.error("Failed to get expiration date from token: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean validateToken(String jwtToken) {
        Assert.notNull(jwtToken, "JWT token must not be null");
        
        try {
            // Validate the JWT token based on the key
            Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(jwtToken);

            return true;
        } catch (Exception e) {
            logger.error("Failed to validate JWT token: " + e.getMessage());
            return false;
        }
    }
}
