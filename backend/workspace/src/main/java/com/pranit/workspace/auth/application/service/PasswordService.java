package com.pranit.workspace.auth.application.service;

import com.pranit.workspace.auth.domain.dto.ChangeForgotPasswordRequest;
import com.pranit.workspace.auth.domain.dto.ChangePassword;
import com.pranit.workspace.auth.domain.dto.ChangePasswordResponse;
import com.pranit.workspace.auth.domain.dto.ForgotPasswordEmail;
import com.pranit.workspace.auth.domain.dto.PasswordResponse;
import jakarta.validation.Valid;

public interface PasswordService {

    PasswordResponse requestPasswordReset(@Valid ForgotPasswordEmail request);

    PasswordResponse resetPassword(@Valid ChangeForgotPasswordRequest request);

    ChangePasswordResponse changeAccountPassword(@Valid ChangePassword request);
}
