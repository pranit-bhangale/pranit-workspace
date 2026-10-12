package com.pranit.workspace.shared.api;

import java.util.UUID;

@FunctionalInterface
public interface CurrentUser {

    UUID userId();
}