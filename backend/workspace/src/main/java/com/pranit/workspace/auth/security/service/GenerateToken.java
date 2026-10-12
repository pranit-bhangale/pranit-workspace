package com.pranit.workspace.auth.security.service;

import com.pranit.workspace.auth.domain.model.UserDetail;

public interface GenerateToken {

    String generateAccessToken(UserDetail userDetail, String jti);

    String generateRefreshToken(UserDetail userDetail, String jti);
}
