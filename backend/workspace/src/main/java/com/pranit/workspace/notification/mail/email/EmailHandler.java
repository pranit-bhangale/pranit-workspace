package com.pranit.workspace.notification.mail.email;


import com.pranit.workspace.notification.api.EmailPurpose;

public interface EmailHandler<T> {

    EmailPurpose supports();

    void send(T event);
}