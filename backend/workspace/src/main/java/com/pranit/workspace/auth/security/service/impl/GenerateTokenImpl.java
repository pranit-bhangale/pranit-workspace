package com.pranit.workspace.auth.security.service.impl;

import com.pranit.workspace.auth.constant.JwtClaim;
import com.pranit.workspace.auth.domain.model.UserDetail;
import com.pranit.workspace.auth.properties.KeysProperties;
import com.pranit.workspace.auth.properties.TokenProperties;
import com.pranit.workspace.auth.security.helper.LoadKey;
import com.pranit.workspace.auth.security.service.GenerateToken;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public final class GenerateTokenImpl implements GenerateToken {

    private final TokenProperties properties;
    private final PrivateKey privateKey;

    public GenerateTokenImpl(TokenProperties properties, KeysProperties keysPath) {
        this.properties = properties;
        this.privateKey = LoadKey.loadPrivateKey(keysPath.privateKeyPath());
    }

    @Override
    public String generateAccessToken(final UserDetail userDetail, final String jti) {
        final String username = userDetail.username();
        final Instant now = Instant.now();
        final Instant expiry = now.plusSeconds(properties.accessToken().expiration());
        final Map<Boolean, Set<String>> grouped = userDetail.authorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .collect(Collectors.partitioningBy(
                        a -> a.startsWith("ROLE_"),
                        Collectors.toUnmodifiableSet()));
        final Set<String> roles = grouped.get(true);
        return Jwts.builder()
                .id(jti)
                .subject(username)
                .issuer(properties.token().issuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claim(JwtClaim.ROLES, roles)
                .claim(JwtClaim.TOKEN_TYPE, JwtClaim.ACCESS)
                .signWith(this.privateKey)
                .compact();
    }

    @Override
    public String generateRefreshToken(final UserDetail userDetail, final String jti) {
        final String username = userDetail.username();
        final Instant now = Instant.now();
        final Instant expiry = now.plusSeconds(properties.refreshToken().expiration());
        return Jwts.builder()
                .id(jti)
                .subject(username)
                .issuer(properties.token().issuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claim(JwtClaim.TOKEN_TYPE, JwtClaim.REFRESH)
                .signWith(this.privateKey)
                .compact();
    }
}
