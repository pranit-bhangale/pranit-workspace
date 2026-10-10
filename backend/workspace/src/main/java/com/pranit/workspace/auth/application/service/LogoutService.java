package com.pranit.workspace.auth.application.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.Optional;

public interface LogoutService {

    Optional<String> readRefreshTokenFromRequest(@Valid HttpServletRequest request);

    void revokedRefreshToken(String refreshToken);

    void clearResponse(@Valid HttpServletResponse response);
}
