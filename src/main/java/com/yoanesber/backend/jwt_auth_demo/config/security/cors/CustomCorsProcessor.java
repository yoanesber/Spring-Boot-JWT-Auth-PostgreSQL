package com.yoanesber.backend.jwt_auth_demo.config.security.cors;

import java.time.Instant;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.cors.*;

public class CustomCorsProcessor extends DefaultCorsProcessor {

    @Override
    protected void rejectRequest(ServerHttpResponse response) throws IOException {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String json = """
            {
                "message": "CORS policy: Origin not allowed by configuration.",
                "error": "CORS Rejected",
                "status": 403,
                "data": null,
                "timestamp": "%s"
            }
        """.formatted(Instant.now().toString());

        response.getBody().write(json.getBytes(StandardCharsets.UTF_8));
        response.flush();
    }
}
