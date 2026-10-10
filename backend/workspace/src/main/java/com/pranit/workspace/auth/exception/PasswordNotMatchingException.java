package com.pranit.workspace.auth.exception;

import com.pranit.workspace.shared.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PasswordNotMatchingException extends BaseException {
    public PasswordNotMatchingException(String message) {
        super(message);
    }
}
