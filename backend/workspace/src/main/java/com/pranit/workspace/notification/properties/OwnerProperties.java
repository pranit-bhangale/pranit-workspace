package com.pranit.workspace.notification.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "workspace.owner")
public record OwnerProperties(
        String email
) {
}
