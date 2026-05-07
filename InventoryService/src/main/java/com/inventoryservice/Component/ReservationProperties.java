package com.inventoryservice.Component;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "reservation")
public record ReservationProperties(
        long expiryMinutes
) {
}
