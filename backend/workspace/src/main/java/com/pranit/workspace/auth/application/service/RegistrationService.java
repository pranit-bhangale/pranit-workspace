package com.pranit.workspace.auth.application.service;

import com.pranit.workspace.auth.domain.dto.SignupRequest;
import com.pranit.workspace.auth.domain.dto.SignupResponse;
import jakarta.validation.Valid;

@FunctionalInterface
public interface RegistrationService {

    SignupResponse createNewAdminAccount(@Valid SignupRequest request);
}
