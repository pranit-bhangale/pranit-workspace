package com.pranit.workspace.notification.redis.service.impl;

import com.pranit.workspace.notification.constant.Redis;
import com.pranit.workspace.shared.helper.Generate;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.ott.DefaultOneTimeToken;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;
import org.springframework.security.authentication.ott.OneTimeTokenService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@NullMarked
@RequiredArgsConstructor
public final class RedisOneTimeTokenService implements OneTimeTokenService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public OneTimeToken generate(final GenerateOneTimeTokenRequest request) {
        final String token = Generate.token();
        final Instant expiresAt = Instant.now().plus(request.getExpiresIn());
        final OneTimeToken ott = new DefaultOneTimeToken(token, request.getUsername(), expiresAt);
        final String key = key(token);
        this.redisTemplate.opsForValue().set(key, request.getUsername(), request.getExpiresIn());
        return ott;
    }

    @Override
    public @Nullable OneTimeToken consume(final OneTimeTokenAuthenticationToken authenticationToken) {
        final String token = authenticationToken.getTokenValue();
        if (token == null) return null;
        final String key = key(token);
        final String username = this.redisTemplate.opsForValue().getAndDelete(key);
        if (username == null) return null;
        return new DefaultOneTimeToken(token, username, Instant.now());
    }

    private String key(final String token) {
        return Redis.KEY_PREFIX + sha256(token);
    }

    private String sha256(final String value) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            final StringBuilder result = new StringBuilder();
            for (byte b : hash) result.append(String.format("%02x", b));
            return result.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }

}
