package com.pranit.workspace.auth.application.service;

import com.pranit.workspace.auth.domain.dto.ProfileMediaResponse;
import com.pranit.workspace.auth.domain.dto.UserUpdateRequest;
import com.pranit.workspace.auth.domain.dto.UserUpdateResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserUpdateResponse updateCurrentUser(UserUpdateRequest request);

    void deleteCurrentUser();

    ProfileMediaResponse uploadProfileMedia(MultipartFile avatar, MultipartFile resume);

    ProfileMediaResponse getProfileMedia();
}
