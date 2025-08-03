package com.yoanesber.backend.jwt_auth_demo.config.security;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

import com.yoanesber.backend.jwt_auth_demo.config.security.cors.CorsFilter;
import com.yoanesber.backend.jwt_auth_demo.config.security.cors.CustomCorsProcessor;
import com.yoanesber.backend.jwt_auth_demo.config.security.jwt.JwtAuthFilter;
import com.yoanesber.backend.jwt_auth_demo.handler.CustomAccessDeniedHandler;
import com.yoanesber.backend.jwt_auth_demo.handler.CustomAuthExceptionHandler;
import com.yoanesber.backend.jwt_auth_demo.service.CustomUserDetailsService;

/*
 * SecurityConfig is a configuration class for Spring Security.
 * It configures authentication, authorization, CORS, and session management for the application.
 * It also sets up a custom JWT authentication filter and exception handling.
 */

@Configuration
@EnableMethodSecurity
(securedEnabled = true, // securedEnabled = true to enable @Secured annotation
jsr250Enabled = true, // jsr250Enabled = true to enable @RolesAllowed annotation
prePostEnabled = false) // prePostEnabled = true to enable @PreAuthorize and @PostAuthorize annotations
public class SecurityConfig {
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthExceptionHandler authExceptionHandler;
    private final CustomUserDetailsService customUserDetailsService;

    @Value("#{'${permit-all-request-url}'.split(',')}")
    private String[] permitAllRequestURL;

    @Value("#{'${cors-allowed-origins}'.split(',')}")
    private List<String> corsAllowedOrigins;

    @Value("#{'${cors-allowed-methods}'.split(',')}")
    private List<String> corsAllowedMethods;

    @Value("#{'${cors-allowed-headers}'.split(',')}")
    private List<String> corsAllowedHeaders;

    @Value("#{'${cors-exposed-headers}'.split(',')}")
    private List<String> corsExposedHeaders;

    @Value("${cors-configuration-endpoint}")
    private String corsConfigurationEndpoint;

    @Value("${cors-allow-credentials}")
    private boolean corsAllowCredentials;

    @Value("${cors-max-age}")
    private Long corsMaxAge;
    
    public SecurityConfig(CustomAccessDeniedHandler accessDeniedHandler,
        CustomAuthExceptionHandler authExceptionHandler,
        CustomUserDetailsService customUserDetailsService) {
        this.accessDeniedHandler = accessDeniedHandler;
        this.authExceptionHandler = authExceptionHandler;
        this.customUserDetailsService = customUserDetailsService;
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsAllowedOrigins.stream().map(String::trim).toList());
        configuration.setAllowedMethods(corsAllowedMethods.stream().map(String::trim).toList());
        configuration.setAllowedHeaders(corsAllowedHeaders.stream().map(String::trim).toList());
        configuration.setExposedHeaders(corsExposedHeaders.stream().map(String::trim).toList());
        configuration.setAllowCredentials(corsAllowCredentials);
        configuration.setMaxAge(corsMaxAge);

        // Create a new UrlBasedCorsConfigurationSource and register the CORS configuration
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(corsConfigurationEndpoint, configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Return a new instance of BCryptPasswordEncoder as the password encoder
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        // Create a new instance of JwtAuthFilter with the custom UserDetailsService
        return new JwtAuthFilter(customUserDetailsService);
    }

    @Bean
    public CorsFilter corsFilter() {
        // Create a new instance of CorsFilter with the CORS configuration source and processor
        return new CorsFilter(corsConfigurationSource(), new CustomCorsProcessor());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // Create an instance of DaoAuthenticationProvider
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
  
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        // Return the authentication manager from the authentication configuration
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .disable()) // Disable CSRF protection because the API is stateless (no sessions) and JWT is stored in HTTP headers (not cookies)
            .cors(cors -> cors
                .disable()) // Disable CORS protection because it is handled by a custom CorsFilter
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Set the session creation policy to stateless (no sessions)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(permitAllRequestURL).permitAll()
                .requestMatchers("/api/v1/netflix-shows/**").hasRole("USER") // Allow requests to the /api/v1/netflix-shows/** endpoint only for users with the USER role
                .anyRequest().authenticated()) // Allow all requests to the permitAllRequestURL without authentication and require authentication for other requests
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authExceptionHandler) // Using CustomAuthExceptionHandler to handle authentication exceptions
                .accessDeniedHandler(accessDeniedHandler)) // Using CustomAccessDeniedHandler to handle access denied exceptions
            .authenticationProvider(authenticationProvider()) // Set the authentication provider
            .addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class) // Add the CorsFilter before the UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class); // Add the JWT filter before the UsernamePasswordAuthenticationFilter
    
        return http.build();
    }
}
