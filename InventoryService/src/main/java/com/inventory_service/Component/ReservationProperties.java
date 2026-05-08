package com.inventory_service.Component;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "reservation")
public record ReservationProperties(
        long expiryMinutes
) {
}
