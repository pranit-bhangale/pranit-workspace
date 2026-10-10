package com.pranit.workspace.auth.security.service;

import io.jsonwebtoken.Claims;

public interface ExtractClaim {

    String getUsernameFromAccessToken(Claims claims);

    String getUsernameFromRefreshToken(Claims claims);

    String getJtiFromAccessToken(Claims claims);

    String getJtiFromRefreshToken(Claims claims);

    Claims validateAndParseToken(String token);

    boolean isRefreshToken(Claims claims);

    boolean isAccessToken(Claims claims);
}
