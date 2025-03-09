package com.yoanesber.spring.security.jwt_auth_postgresql.config;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.yoanesber.spring.security.jwt_auth_postgresql.handler.CustomAccessDeniedHandler;
import com.yoanesber.spring.security.jwt_auth_postgresql.handler.CustomAuthExceptionHandler;
import com.yoanesber.spring.security.jwt_auth_postgresql.handler.JwtAuthFilterHandler;
import com.yoanesber.spring.security.jwt_auth_postgresql.service.CustomUserDetailsService;
import com.yoanesber.spring.security.jwt_auth_postgresql.service.JwtService;

@Configuration
@EnableMethodSecurity
(securedEnabled = true, // securedEnabled = true to enable @Secured annotation
jsr250Enabled = true, // jsr250Enabled = true to enable @RolesAllowed annotation
prePostEnabled = false) // prePostEnabled = true to enable @PreAuthorize and @PostAuthorize annotations
public class SecurityConfig {

    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthExceptionHandler authExceptionHandler;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;

    @Value("#{'${permit-all-request-url}'.split(',')}")
    private String[] permitAllRequestURL;

    @Value("#{'${cors-allowed-origins}'.split(',')}")
    private List<String> corsAllowedOrigin;

    @Value("#{'${cors-allowed-methods}'.split(',')}")
    private List<String> corsAllowedMethods;

    @Value("#{'${cors-allowed-headers}'.split(',')}")
    private List<String> corsAllowedHeaders;

    @Value("${cors-allow-credentials}")
    private boolean corsAllowCredentials;

    @Value("${cors-max-age}")
    private Long corsMaxAge;

    @Value("#{'${cors-exposed-headers}'.split(',')}")
    private List<String> corsExposedHeaders;

    @Value("${cors-configuration-endpoint}")
    private String corsConfigurationEndpoint;
    
    public SecurityConfig(CustomAccessDeniedHandler accessDeniedHandler,
        CustomAuthExceptionHandler authExceptionHandler,
        CustomUserDetailsService customUserDetailsService,
        JwtService jwtService) {
        this.accessDeniedHandler = accessDeniedHandler;
        this.authExceptionHandler = authExceptionHandler;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
    }

    @Bean
    public JwtAuthFilterHandler jwtAuthenticationFilter() {
        return new JwtAuthFilterHandler(jwtService, customUserDetailsService);
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
    public PasswordEncoder passwordEncoder() {
        // Return a new instance of BCryptPasswordEncoder as the password encoder
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Create a new instance of CorsConfiguration and set the allowed origins, methods, headers, etc
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsAllowedOrigin); // Allow requests from your client
        configuration.setAllowedMethods(corsAllowedMethods); // Allowed HTTP methods
        configuration.setAllowedHeaders(corsAllowedHeaders); // Allowed headers
        configuration.setAllowCredentials(corsAllowCredentials); // Allow credentials if needed
        configuration.setMaxAge(corsMaxAge); // Set how long the response will be cached by the browser
        configuration.setExposedHeaders(corsExposedHeaders); // Expose the headers to the client

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(corsConfigurationEndpoint, configuration); // Apply this CORS config to all endpoints
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .disable()) // Disable CSRF protection because the API is stateless (no sessions) and JWT is stored in HTTP headers (not cookies)
            .cors(cors -> cors
                .configurationSource(corsConfigurationSource())) // Enable CORS with custom configuration source
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Set the session creation policy to stateless (no sessions)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(permitAllRequestURL).permitAll()
                .requestMatchers("/api/v1/netflix-shows/**").hasRole("USER") // Allow requests to the /api/v1/netflix-shows/** endpoint only for users with the USER role
                .anyRequest().authenticated()) // Allow all requests to the permitAllRequestURL without authentication and require authentication for other requests
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authExceptionHandler) // Set the authentication entry point to the custom AuthExceptionHandler
                .accessDeniedHandler(accessDeniedHandler)) // Set the access denied handler to the custom AuthExceptionHandler
            .authenticationProvider(authenticationProvider()) // Set the authentication provider
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // Add the JWT filter before the UsernamePasswordAuthenticationFilter
    
        return http.build();
    }
}
