package com.pranit.workspace.auth.application.service;

import com.pranit.workspace.auth.domain.dto.UserUpdateRequest;
import com.pranit.workspace.auth.domain.dto.UserUpdateResponse;

public interface UserService {

    UserUpdateResponse updateCurrentUser(UserUpdateRequest request);

    void deleteCurrentUser();
}
