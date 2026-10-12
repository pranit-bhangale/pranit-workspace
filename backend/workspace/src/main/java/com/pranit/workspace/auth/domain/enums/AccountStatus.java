package com.pranit.workspace.auth.domain.enums;

import lombok.Getter;

/**
 * Account lifecycle states.
 */
@Getter
public enum AccountStatus {

    ACTIVE("Active", "The account is active and can be used normally"),
    SUSPENDED("Suspended", "The account is temporarily suspended"),
    PENDING_VERIFICATION("Pending Verification", "The account is awaiting verification");

    private final String name;
    private final String description;

    AccountStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
