package com.pranit.workspace.notification.api;

import com.pranit.workspace.notification.mail.event.EmailEvent;

public interface EmailRouter {

    void send(EmailEvent event, EmailPurpose purpose);
}
