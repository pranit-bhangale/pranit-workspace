package com.pranit.workspace.auth.exception;

import com.pranit.workspace.shared.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class KeyResourceNotFoundException extends BaseException {
    public KeyResourceNotFoundException(String message) {
        super(message);
    }
}
