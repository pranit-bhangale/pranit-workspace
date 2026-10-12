package com.pranit.workspace.auth.exception;

import com.pranit.workspace.shared.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenOwnerException extends BaseException {
    public TokenOwnerException(String message) {
        super(message);
    }
}
