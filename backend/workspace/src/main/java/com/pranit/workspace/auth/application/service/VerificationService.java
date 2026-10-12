package com.pranit.workspace.auth.application.service;

import com.pranit.workspace.auth.domain.dto.VerificationResponse;
import com.pranit.workspace.auth.domain.dto.VerifyOtp;
import jakarta.validation.Valid;

public interface VerificationService {

    VerificationResponse verifyRegistration(@Valid VerifyOtp request);

    VerificationResponse verifyPasswordResetOtp(@Valid VerifyOtp request);
}
