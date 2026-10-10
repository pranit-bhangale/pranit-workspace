package com.pranit.workspace.auth.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.Set;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponse(
        Boolean mfaRequired,
        String username,
        Set<String> roles,
        String message
) {
}
