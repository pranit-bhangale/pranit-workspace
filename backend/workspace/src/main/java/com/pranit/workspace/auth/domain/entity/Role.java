package com.pranit.workspace.auth.domain.entity;

import lombok.Getter;

/**
 * Access roles for portfolio system users.
 */
@Getter
public enum Role {

    ADMIN("Admin", "Portfolio Owner with full administrative access"),
    EDITOR("Editor", "Content manager for blogs and projects"),
    USER("User", "Authenticated registered user");

    private final String name;
    private final String description;

    Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
