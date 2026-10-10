package com.pranit.workspace.auth.application.service.impl;

import com.pranit.workspace.auth.application.repository.RefreshTokenRepository;
import com.pranit.workspace.auth.application.service.LogoutService;
import com.pranit.workspace.auth.domain.entity.RefreshToken;
import com.pranit.workspace.auth.properties.CookieProperties;
import com.pranit.workspace.auth.security.service.CookieService;
import com.pranit.workspace.auth.security.service.ExtractClaim;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {

    private final CookieProperties cookieProperties;
    private final ExtractClaim extractClaim;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieService cookieService;

    @Override
    public Optional<String> readRefreshTokenFromRequest(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) return Optional.empty();
        return Arrays.stream(cookies)
                .filter(cookie -> this.cookieProperties.name().refreshTokenName().equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> value != null && !value.isBlank())
                .findFirst();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void revokedRefreshToken(final String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) return;
        final Claims claims;
        try {
            claims = this.extractClaim.validateAndParseToken(refreshToken);
        } catch (JwtException ex) {
            log.error("Invalid refresh token during logout: {}", ex.getMessage());
            return;
        }
        if (!this.extractClaim.isRefreshToken(claims)) return;
        final String jti = this.extractClaim.getJtiFromRefreshToken(claims);
        final String username = this.extractClaim.getUsernameFromRefreshToken(claims);
        if (jti == null || jti.isBlank()) return;
        if (username == null || username.isBlank()) return;
        final RefreshToken token = this.refreshTokenRepository.findByJtiForUpdate(jti).orElse(null);
        if (token == null) return;
        if (token.getUser() == null || !token.getUser().getUsername().equals(username)) return;
        this.refreshTokenRepository.revokeAllByUserId(token.getUser().getUserId());
        log.info("Logout successful userId: {}", token.getUser().getUserId());
    }

    @Override
    public void clearResponse(final HttpServletResponse response) {
        this.cookieService.clearAccessTokenCookie(response);
        this.cookieService.clearRefreshTokenCookie(response);
        this.cookieService.addNoStoreHeaders(response);
        SecurityContextHolder.clearContext();
    }
}
