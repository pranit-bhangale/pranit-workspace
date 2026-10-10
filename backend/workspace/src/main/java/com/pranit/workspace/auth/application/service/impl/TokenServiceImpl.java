package com.pranit.workspace.auth.application.service.impl;

import com.pranit.workspace.auth.application.repository.RefreshTokenRepository;
import com.pranit.workspace.auth.application.service.TokenService;
import com.pranit.workspace.auth.application.service.UserDetailService;
import com.pranit.workspace.auth.domain.entity.RefreshToken;
import com.pranit.workspace.auth.domain.model.UserDetail;
import com.pranit.workspace.auth.exception.TokenExpiredException;
import com.pranit.workspace.auth.exception.UnauthorizedException;
import com.pranit.workspace.auth.properties.CookieProperties;
import com.pranit.workspace.auth.properties.TokenProperties;
import com.pranit.workspace.auth.security.service.CookieService;
import com.pranit.workspace.auth.security.service.ExtractClaim;
import com.pranit.workspace.auth.security.service.GenerateToken;
import com.pranit.workspace.shared.helper.Generate;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final GenerateToken generateToken;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ExtractClaim extractClaim;
    private final CookieService cookieService;
    private final CookieProperties cookieProperties;
    private final TokenProperties tokenProperties;
    private final UserDetailService userDetailService;

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
    public void generateNewRefreshToken(final String refreshToken, final HttpServletResponse response) {
        final Claims claims = this.extractClaim.validateAndParseToken(refreshToken);
        if (!this.extractClaim.isRefreshToken(claims)) {
            throw new UnauthorizedException("Invalid token");
        }
        final String jti = this.extractClaim.getJtiFromRefreshToken(claims);
        final String username = this.extractClaim.getUsernameFromRefreshToken(claims);
        if (jti == null || jti.isBlank()) {
            throw new UnauthorizedException("Invalid refresh token");
        }
        if (username == null || username.isBlank()) return;
        log.debug("Refresh request jti: {} | username: {}", jti, username);
        final RefreshToken existingToken = this.refreshTokenRepository.findByJtiForUpdate(jti)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found jti: {}", jti);
                    return new UnauthorizedException("Invalid refresh token");
                });
        if (existingToken.getUser() == null || !existingToken.getUser().getUsername().equals(username)) {
            return;
        }
        if (existingToken.isRevoked()) {
            this.refreshTokenRepository.revokeAllByUserId(existingToken.getUser().getUserId());
            throw new UnauthorizedException("Token reuse detected");
        }
        final Instant now = Instant.now();
        if (existingToken.getExpiresAt().isAfter(now)) {
            existingToken.setRevoked(true);
            throw new TokenExpiredException("Refresh token expired");
        }

        final UserDetail userDetail = (UserDetail) this.userDetailService.loadUserByUsername(existingToken.getUser().getUsername());
        final String newJti = Generate.jti();
        final Duration accessTokenTtl = Duration.ofSeconds(this.tokenProperties.accessToken().expiration());
        final Duration refreshTokenTtl = Duration.ofSeconds(this.tokenProperties.refreshToken().expiration());
        final String newAccessToken = this.generateToken.generateAccessToken(userDetail, newJti);
        final String newRefreshToken = this.generateToken.generateRefreshToken(userDetail, newJti);
        existingToken.setRevoked(true);
        existingToken.setReplacedByToken(newJti);
        final RefreshToken rotatedToken = RefreshToken.builder()
                .jti(newJti)
                .user(existingToken.getUser())
                .expiresAt(now.plus(refreshTokenTtl))
                .revoked(false)
                .build();
        this.refreshTokenRepository.save(rotatedToken);
        this.cookieService.attachAccessTokenCookie(response, newAccessToken, accessTokenTtl);
        this.cookieService.attachRefreshTokenCookie(response, newRefreshToken, refreshTokenTtl);
        this.cookieService.addNoStoreHeaders(response);
        log.info("Refresh token rotated successfully userId: {} | oldJti: {} | newJti: {}", rotatedToken.getUser().getUserId(), jti, newJti);
    }
}
