package com.pranit.workspace.auth.security.endpoints.impl;

import com.pranit.workspace.auth.security.endpoints.PublicEndpointProvider;
import org.springframework.stereotype.Service;

@Service
public class PublicEndpointProviderImpl implements PublicEndpointProvider {

    private static final String[] PUBLIC_ENDPOINTS = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/auth/login",
            "/api/auth/mfa/ott",
            "/api/auth/refresh",
            "/api/auth/register",
            "/api/auth/register/**",
            "/api/auth/verify/**",
            "/api/auth/password/request-reset",
            "/api/auth/password/reset",
            "/api/contact",
            "/api/blogs/published",
            "/api/blogs/slug/**"
    };

    @Override
    public String[] publicEndpoints() {
        return PUBLIC_ENDPOINTS;
    }
}
