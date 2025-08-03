package com.yoanesber.backend.jwt_auth_demo.config.shutdown;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * TomcatGracefulShutdown is a configuration class that handles graceful shutdown of the Tomcat server.
 * It pauses the connector and shuts down the executor service gracefully when the application context is closed.
 */
@Component
public class TomcatGracefulShutdown {
    private volatile Connector connector;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(connector -> TomcatGracefulShutdown.this.connector = connector);
        return factory;
    }
    
    @PreDestroy
    public void onShutdown() {
        System.out.println("[SHUTDOWN] Application is shutting down gracefully...");

        try {
            if (this.connector != null) {
                this.connector.pause();
                System.out.println("[SHUTDOWN] Connector paused.");
            }

            this.executorService.shutdown();

            if (!this.executorService.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                System.err.println("[SHUTDOWN] Executor did not terminate in the specified time. Then forcing shutdown now.");
                this.executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.println("[SHUTDOWN] Interrupted during shutdown, forcing shutdown now.");
            this.executorService.shutdownNow();
        } catch (Exception e) {
            System.err.println("[SHUTDOWN] Exception during shutdown: " + e.getMessage());
        }

        System.out.println("[SHUTDOWN] Application has shut down gracefully.");
    }
}
