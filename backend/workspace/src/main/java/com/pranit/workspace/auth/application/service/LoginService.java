package com.pranit.workspace.auth.application.service;

import com.pranit.workspace.auth.domain.dto.LoginRequest;
import com.pranit.workspace.auth.domain.dto.LoginResponse;
import com.pranit.workspace.auth.domain.dto.MfaTokenRequest;
import com.pranit.workspace.auth.domain.dto.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface LoginService {

    LoginResponse authenticateUser(@Valid LoginRequest request, @Valid HttpServletRequest httpRequest);

    LoginResponse verifyMfa(@Valid MfaTokenRequest request, @Valid HttpServletRequest httpRequest, @Valid HttpServletResponse response);

    UserResponse getCurrentUser();
}
