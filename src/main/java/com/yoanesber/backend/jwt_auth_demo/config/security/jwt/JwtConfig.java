package com.yoanesber.backend.jwt_auth_demo.config.security.jwt;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * JwtConfig is a configuration class for JWT (JSON Web Token) authentication.
 * This class initializes static variables with the configuration values
 * so that they can be accessed statically throughout the application.
 * It is annotated with @Configuration to indicate that it is a Spring configuration class.
 * The properties are injected from the application properties file using @Value annotations.
 */
@Configuration
public class JwtConfig {
    // Configuration properties for JWT authentication
    @Value("${jwt.cookie.http-only}")
    private boolean cookieHttpOnly;

    @Value("${jwt.cookie.max-age-ms}")
    private Long cookieMaxAgeMs;

    @Value("${jwt.cookie.path}")
    private String cookiePath;

    @Value("${jwt.cookie.same-site}")
    private String cookieSameSite;

    @Value("${jwt.cookie.secure}")
    private boolean cookieSecure;

    @Value("${jwt.cookie.response-enabled}")
    private boolean cookieResponseEnabled;

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.key-algorithm}")
    private String keyAlgorithm;

    @Value("${jwt.key-secret}")
    private String keySecret;

    @Value("${jwt.private-key-file}")
    private String privateKeyFile;

    @Value("${jwt.public-key-file}")
    private String publicKeyFile;

    @Value("${jwt.token.type}")
    private String tokenType;

    @Value("${jwt.expiration-ms}")
    private Long expirationMs;

    @Value("${jwt.cookie.name}")
    private String cookieName;

    // Static variables to hold the configuration values
    private static boolean staticCookieHttpOnly;
    private static Long staticCookieMaxAgeMs;
    private static String staticCookiePath;
    private static String staticCookieSameSite;
    private static boolean staticCookieSecure;
    private static boolean staticCookieResponseEnabled;
    private static String staticHeader;
    private static String staticIssuer;
    private static String staticKeyAlgorithm;
    private static String staticKeySecret;
    private static String staticPrivateKeyFile;
    private static String staticPublicKeyFile;
    private static String staticTokenType;
    private static Long staticExpirationMs;
    private static String staticCookieName;

    @PostConstruct
    public void init() {
        staticCookieHttpOnly = this.cookieHttpOnly;
        staticCookieMaxAgeMs = this.cookieMaxAgeMs;
        staticCookiePath = this.cookiePath;
        staticCookieSameSite = this.cookieSameSite;
        staticCookieSecure = this.cookieSecure;
        staticCookieResponseEnabled = this.cookieResponseEnabled;
        staticHeader = this.header;
        staticIssuer = this.issuer;
        staticKeyAlgorithm = this.keyAlgorithm;
        staticKeySecret = this.keySecret;
        staticPrivateKeyFile = this.privateKeyFile;
        staticPublicKeyFile = this.publicKeyFile;
        staticTokenType = this.tokenType;
        staticExpirationMs = this.expirationMs;
        staticCookieName = this.cookieName;
    }

    public static boolean getStaticCookieHttpOnly() {
        return staticCookieHttpOnly;
    }
    public static Long getStaticCookieMaxAgeMs() {
        return staticCookieMaxAgeMs;
    }
    public static String getStaticCookiePath() {
        return staticCookiePath;
    }
    public static String getStaticCookieSameSite() {
        return staticCookieSameSite;
    }
    public static boolean getStaticCookieSecure() {
        return staticCookieSecure;
    }
    public static boolean getStaticCookieResponseEnabled() {
        return staticCookieResponseEnabled;
    }
    public static String getStaticHeader() {
        return staticHeader;
    }
    public static String getStaticIssuer() {
        return staticIssuer;
    }
    public static String getStaticKeyAlgorithm() {
        return staticKeyAlgorithm;
    }
    public static String getStaticKeySecret() {
        return staticKeySecret;
    }
    public static String getStaticPrivateKeyFile() {
        return staticPrivateKeyFile;
    }
    public static String getStaticPublicKeyFile() {
        return staticPublicKeyFile;
    }
    public static String getStaticTokenType() {
        return staticTokenType;
    }
    public static Long getStaticExpirationMs() {
        return staticExpirationMs;
    }
    public static String getStaticCookieName() {
        return staticCookieName;
    }
}
