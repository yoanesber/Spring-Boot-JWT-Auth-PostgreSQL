package com.yoanesber.spring.security.jwt_auth_postgresql.util;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.security.*;
import javax.crypto.SecretKey;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.yoanesber.spring.security.jwt_auth_postgresql.config.security.JwtConfig;
import com.yoanesber.spring.security.jwt_auth_postgresql.entity.JwtClaim;

/**
 * JwtUtil is a utility class that provides methods for generating, parsing, and validating JWT tokens.
 * It handles both HMAC and RSA algorithms for signing and verifying JWTs.
 * The class also provides methods to extract JWT tokens from HTTP headers and cookies.
 */
@Component
public class JwtUtil {
    private static boolean jwtCookieHttpOnly = JwtConfig.getStaticCookieHttpOnly();
    private static Long jwtCookieMaxAgeMs = JwtConfig.getStaticCookieMaxAgeMs();
    private static String jwtCookiePath = JwtConfig.getStaticCookiePath();
    private static String jwtCookieSameSite = JwtConfig.getStaticCookieSameSite();
    private static boolean jwtCookieSecure = JwtConfig.getStaticCookieSecure();
    private static String jwtHeader = JwtConfig.getStaticHeader();
    private static String jwtIssuer = JwtConfig.getStaticIssuer();
    private static String jwtKeyAlgorithm = JwtConfig.getStaticKeyAlgorithm();
    private static String jwtKeySecret = JwtConfig.getStaticKeySecret();
    private static String jwtPrivateKeyFile = JwtConfig.getStaticPrivateKeyFile();
    private static String jwtPublicKeyFile = JwtConfig.getStaticPublicKeyFile();
    private static String jwtTokenType = JwtConfig.getStaticTokenType();
    private static Long jwtExpirationMs = JwtConfig.getStaticExpirationMs();
    private static String jwtCookieName = JwtConfig.getStaticCookieName();

    public static String getJwtFromHeader(HttpServletRequest request) {
        Assert.notNull(request, "Request must not be null");
        
        // Get the Authorization value from the request header based on the header name
        String headerAuth = request.getHeader(jwtHeader);

        // Check if the Authorization value is not null and starts with the JWT prefix
        if (headerAuth != null && headerAuth.startsWith(jwtTokenType))
            return headerAuth.substring(jwtTokenType.length()).trim();

        return "";
    }

    public static String getJwtFromCookies(HttpServletRequest request) {
        Assert.notNull(request, "Request must not be null");
        
        // Get the JWT token from the request cookies based on the cookie name
        return request.getCookies() != null ? 
            java.util.Arrays.stream(request.getCookies())
                .filter(cookie -> jwtCookieName.equals(cookie.getName()))
                .map(cookie -> cookie.getValue())
                .findFirst()
                .orElse("") : "";
    }

    private static SecretKey key() {
        // Generate the HMAC SHA key from the JWT secret
        return Keys.hmacShaKeyFor(jwtKeySecret.getBytes(StandardCharsets.UTF_8));
    }

    private static PrivateKey getPrivateKey() throws Exception {
        try {
            String key = Files.readString(Paths.get(jwtPrivateKeyFile))
                    .replaceAll("-----\\w+ PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] decoded = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        } catch (Exception e) {
            throw e;
        }
    }

    private static PublicKey getPublicKey() throws Exception {
        try {
            String key = Files.readString(Paths.get(jwtPublicKeyFile))
                    .replaceAll("-----\\w+ PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] decoded = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        } catch (Exception e) {
            throw e;
        }
    }

    public static String generateJwtToken(JwtClaim claim) throws JwtException, RuntimeException {
        Assert.notNull(claim, "Claim must not be null");

        // Generate the JWT token based on the username
        JwtBuilder jwtBuilder = Jwts.builder()
            .setSubject(claim.getUserName())
            .claim("userId", claim.getUserId())
            .claim("email", claim.getEmail())
            .claim("firstName", claim.getFirstName())
            .claim("lastName", claim.getLastName())
            .claim("userType", claim.getUserType())
            .claim("roles", claim.getRoles())
            .setIssuer(jwtIssuer)
            .setIssuedAt(new Date())
            .setExpiration(new Date(new Date().getTime() + jwtExpirationMs));

        // Check if the key algorithm is RSA
        if ("RSA".equalsIgnoreCase(jwtKeyAlgorithm)) {
            try {
                // Sign the JWT token with the private key
                return jwtBuilder.signWith(getPrivateKey(), SignatureAlgorithm.RS256).compact();
            } catch (JwtException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Failed to sign JWT token with RSA: " + e.getMessage());
            }
        } else if ("HMAC".equalsIgnoreCase(jwtKeyAlgorithm)) {
            try {
                // Sign the JWT token with the HMAC SHA key
                return jwtBuilder.signWith(key(), SignatureAlgorithm.HS256).compact();
            } catch (JwtException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Failed to sign JWT token with HMAC: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Unsupported key algorithm: " + jwtKeyAlgorithm);
        }
    }

    public static ResponseCookie generateJwtCookie(JwtClaim claim) {
        Assert.notNull(claim, "Claim must not be null");

        // Generate the JWT token from the user principal
        String jwtToken = generateJwtToken(claim);

        // Generate the JWT cookie based on the JWT token
        return ResponseCookie.from(jwtCookieName, jwtToken)
            .path(jwtCookiePath)
            .maxAge(jwtCookieMaxAgeMs)
            .secure(jwtCookieSecure)
            .httpOnly(jwtCookieHttpOnly)
            .sameSite(jwtCookieSameSite)
            .build();
    }

    public static ResponseCookie getCleanJwtCookie() {
        // Generate the clean JWT cookie (remove the JWT token)
        return ResponseCookie.from(jwtCookieName, "")
            .path(jwtCookiePath)
            .maxAge(0)
            .secure(jwtCookieSecure)
            .httpOnly(jwtCookieHttpOnly)
            .sameSite(jwtCookieSameSite)
            .build();
    }

    public static String getUserNameFromToken(String jwtToken) throws JwtException, RuntimeException {
        Assert.notNull(jwtToken, "JWT token must not be null");

        JwtParserBuilder jwtParserBuilder = Jwts.parserBuilder();

        // Check if the key algorithm is RSA
        if ("RSA".equalsIgnoreCase(jwtKeyAlgorithm)) {
            try {
                // Set the public key for RSA algorithm
                return jwtParserBuilder.setSigningKey(getPublicKey())
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody()
                    .getSubject();
            } catch (JwtException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Failed to set public key for JWT parsing: " + e.getMessage());
            }
        } else if ("HMAC".equalsIgnoreCase(jwtKeyAlgorithm)) {
            try {
                // Set the HMAC SHA key for HMAC algorithm
                return jwtParserBuilder.setSigningKey(key())
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody()
                    .getSubject();
            } catch (JwtException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Failed to set HMAC key for JWT parsing: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Unsupported key algorithm: " + jwtKeyAlgorithm);
        }
    }

    public static Date getExpirationDateFromToken(String jwtToken) throws JwtException, RuntimeException {
        Assert.notNull(jwtToken, "JWT token must not be null");

        JwtParserBuilder jwtParserBuilder = Jwts.parserBuilder();

        // Check if the key algorithm is RSA
        if ("RSA".equalsIgnoreCase(jwtKeyAlgorithm)) {
            try {
                // Set the public key for RSA algorithm
                return jwtParserBuilder.setSigningKey(getPublicKey())
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody()
                    .getExpiration();
            } catch (JwtException e) {
                throw e;
            }  catch (Exception e) {
                throw new RuntimeException("Failed to set public key for JWT parsing: " + e.getMessage());
            }
        } else if ("HMAC".equalsIgnoreCase(jwtKeyAlgorithm)) {
            try {
                // Set the HMAC SHA key for HMAC algorithm
                return jwtParserBuilder.setSigningKey(key())
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody()
                    .getExpiration();
            } catch (JwtException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Failed to set HMAC key for JWT parsing: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Unsupported key algorithm: " + jwtKeyAlgorithm);
        }
    }

    public static boolean validateToken(String jwtToken) throws JwtException, RuntimeException {
        Assert.notNull(jwtToken, "JWT token must not be null");

        JwtParserBuilder jwtParserBuilder = Jwts.parserBuilder();

        // Check if the key algorithm is RSA
        if ("RSA".equalsIgnoreCase(jwtKeyAlgorithm)) {
            try {
                // Set the public key for RSA algorithm
                jwtParserBuilder.setSigningKey(getPublicKey())
                    .build()
                    .parseClaimsJws(jwtToken);

                return true;
            } catch (JwtException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Failed to set public key for JWT parsing: " + e.getMessage());
            }
        } else if ("HMAC".equalsIgnoreCase(jwtKeyAlgorithm)) {
            try {
                // Set the HMAC SHA key for HMAC algorithm
                jwtParserBuilder.setSigningKey(key())
                    .build()
                    .parseClaimsJws(jwtToken);
                
                return true;
            } catch (JwtException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Failed to set public key for JWT parsing: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Unsupported key algorithm: " + jwtKeyAlgorithm);
        }
    }
}
