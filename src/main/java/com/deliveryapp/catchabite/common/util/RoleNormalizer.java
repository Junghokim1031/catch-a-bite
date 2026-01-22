package com.deliveryapp.catchabite.common.util;

import com.deliveryapp.catchabite.common.constant.RoleConstant;

public final class RoleNormalizer {
    private RoleNormalizer() {
    }

    public static String normalize(String roleName) {
        if (roleName == null) {
            throw new IllegalArgumentException("roleName must not be null");
        }
        String trimmed = roleName.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("roleName must not be blank");
        }
        if (trimmed.startsWith("ROLE_")) {
            return trimmed;
        }

        String upper = trimmed.toUpperCase();
        if ("USER".equals(upper)) {
            return RoleConstant.ROLE_USER;
        }
        if ("RIDER".equals(upper)) {
            return RoleConstant.ROLE_RIDER;
        }
        if ("STORE_OWNER".equals(upper)) {
            return RoleConstant.ROLE_STORE_OWNER;
        }

        throw new IllegalArgumentException("Unknown role: " + trimmed);
    }
}
