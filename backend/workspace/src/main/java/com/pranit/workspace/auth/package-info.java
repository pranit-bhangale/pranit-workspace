@ApplicationModule(
        displayName = "AUTH",
        allowedDependencies = {"shared", "notification::api", "minio::api"}
)
package com.pranit.workspace.auth;

import org.springframework.modulith.ApplicationModule;