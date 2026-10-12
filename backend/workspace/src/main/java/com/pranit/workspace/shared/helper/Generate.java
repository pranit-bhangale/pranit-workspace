package com.pranit.workspace.shared.helper;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@UtilityClass
public class Generate {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String eventId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String jti() {
        return randomBytes(64);
    }

    private static String randomBytes(int length) {
        byte[] bytes = new byte[length];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    public static String token() {
        return randomBytes(32);
    }

    public static String challengeId() {
        return randomBytes(6);
    }
}
