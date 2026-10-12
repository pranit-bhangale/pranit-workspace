package com.pranit.workspace.auth.security.service.impl;

import com.pranit.workspace.auth.constant.JwtClaim;
import com.pranit.workspace.auth.properties.KeysProperties;
import com.pranit.workspace.auth.security.helper.LoadKey;
import com.pranit.workspace.auth.security.service.ExtractClaim;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.PublicKey;

@Slf4j
@Service
public final class ExtractClaimImpl implements ExtractClaim {

    private final PublicKey publicKey;

    public ExtractClaimImpl(KeysProperties properties) {
        this.publicKey = LoadKey.loadPublicKey(properties.publicKeyPath());
    }

    @Override
    public String getUsernameFromAccessToken(final Claims claims) {
        return claims.getSubject();
    }

    @Override
    public String getUsernameFromRefreshToken(Claims claims) {
        return claims.getSubject();
    }

    @Override
    public String getJtiFromAccessToken(Claims claims) {
        return claims.getId();
    }

    @Override
    public String getJtiFromRefreshToken(final Claims claims) {
        return claims.getId();
    }

    @Override
    public Claims validateAndParseToken(final String token) {
        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            log.error("JWT validation failed: {}", ex.getMessage());
            throw new JwtException("Invalid or expired JWT", ex);
        }
    }

    @Override
    public boolean isRefreshToken(final Claims claims) {
        return JwtClaim.REFRESH.equals(claims.get(JwtClaim.TOKEN_TYPE, String.class));
    }

    @Override
    public boolean isAccessToken(final Claims claims) {
        return JwtClaim.ACCESS.equals(claims.get(JwtClaim.TOKEN_TYPE, String.class));
    }
}
