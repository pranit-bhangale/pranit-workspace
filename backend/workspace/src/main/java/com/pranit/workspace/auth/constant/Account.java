package com.pranit.workspace.auth.constant;

import java.time.Duration;

public final class Account {

    public static final String MFA_USERNAME = "MFA_USERNAME";

    public static final String MFA_USER_ID = "MFA_USER_ID";

    public static final Duration MFA_TTL = Duration.ofMinutes(5);

    private Account() {
    }
}
