package com.pranit.workspace.notification.mail.exception;

import com.pranit.workspace.shared.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidEmailEventException extends BaseException {
    public InvalidEmailEventException(String message) {
        super(message);
    }
}
