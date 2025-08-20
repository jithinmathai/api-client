package com.test.demo.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ExternalApiHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Basic placeholder checks
        return Health.up()
            .withDetail("externalApi", "reachable")
            .withDetail("activeSessions", 0)
            .build();
    }
}


