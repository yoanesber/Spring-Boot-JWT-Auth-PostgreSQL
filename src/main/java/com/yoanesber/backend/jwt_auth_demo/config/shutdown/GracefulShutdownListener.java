package com.yoanesber.backend.jwt_auth_demo.config.shutdown;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * GracefulShutdownListener is a listener that handles the graceful shutdown of the application.
 * It listens for the ContextClosedEvent and prints a message when the application context is closed.
 * This can be useful for logging or performing cleanup tasks during shutdown.
 */
@Component
public class GracefulShutdownListener implements ApplicationListener<ContextClosedEvent> {
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        System.out.println("[SHUTDOWN] ContextClosedEvent received");
        System.out.printf("[SHUTDOWN] Application %s is shutting down gracefully...%n", event.getApplicationContext().getId());
    }
}