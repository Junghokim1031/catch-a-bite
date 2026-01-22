package com.deliveryapp.catchabite.auth;

import java.io.Serializable;
import java.security.Principal;

/**
 * AuthUser: authenticated principal that stores account type and login key.
 */
public record AuthUser(String accountType, String loginKey) implements Principal, Serializable {

    public AuthUser {
        if (accountType == null || accountType.isBlank()) {
            throw new IllegalArgumentException("accountType is required.");
        }
        if (loginKey == null || loginKey.isBlank()) {
            throw new IllegalArgumentException("loginKey is required.");
        }
        accountType = accountType.trim().toUpperCase();
        loginKey = loginKey.trim();
    }

    @Override
    public String getName() {
        return accountType + ":" + loginKey;
    }
}
