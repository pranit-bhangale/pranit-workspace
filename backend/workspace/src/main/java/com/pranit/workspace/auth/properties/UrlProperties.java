package com.pranit.workspace.auth.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "workspace.ui")
public record UrlProperties(
        String url,
        String path
) {
}
