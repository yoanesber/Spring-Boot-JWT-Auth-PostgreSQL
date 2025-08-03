package com.yoanesber.backend.jwt_auth_demo.config.security.cors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.cors.*;
import org.springframework.web.filter.OncePerRequestFilter;

import com.yoanesber.backend.jwt_auth_demo.util.ResponseUtil;

public class CorsFilter extends OncePerRequestFilter {
    private final CorsConfigurationSource configSource;
    private final CorsProcessor processor;

    public CorsFilter(CorsConfigurationSource configSource, CorsProcessor processor) {
        this.configSource = configSource;
        this.processor = processor;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        // if (!CorsUtils.isCorsRequest(request)) {
        //     filterChain.doFilter(request, response);
        //     return;
        // }

        String origin = request.getHeader(HttpHeaders.ORIGIN);

        try {
            if (origin == null || origin.isBlank()) {
                ResponseUtil.buildResponse(request, response, HttpStatus.FORBIDDEN, "Missing Origin",
                        "CORS policy: The request does not have an Origin header.", null);
                return;
            }

            URI parsed = new URI(origin);
            String scheme = parsed.getScheme();
            if (scheme == null || (!scheme.equals("http") && !scheme.equals("https"))) {
                ResponseUtil.buildResponse(request, response, HttpStatus.FORBIDDEN, "Invalid Origin",
                        "CORS policy: Only HTTP and HTTPS protocols are allowed.", null);
                return;
            }
        } catch (MalformedURLException e) {
            ResponseUtil.buildResponse(request, response, HttpStatus.FORBIDDEN, "Invalid Origin",
                    "CORS policy: The request's origin is not a valid URL.", null);
            return;
        } catch (Exception ex) {
            ResponseUtil.buildResponse(request, response, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                    "CORS policy: An unexpected error occurred due to: " + ex.getMessage(), null);
            return;
        }

        CorsConfiguration config = this.configSource.getCorsConfiguration(request);

        try {
            if (this.processor.processRequest(config, request, response)) {
                filterChain.doFilter(request, response);
                return;
            } 
        } catch (AuthorizationDeniedException e) {
            ResponseUtil.buildResponse(request, response, HttpStatus.FORBIDDEN, "CORS Rejected",
                    "CORS policy: Access denied due to authorization failure.", null);
            return;
        } catch (Exception e) {
            ResponseUtil.buildResponse(request, response, HttpStatus.INTERNAL_SERVER_ERROR, "CORS Error",
                    "CORS policy: An error occurred while processing the request due to: " + e.getMessage(), null);
            return;
        }
    }
}