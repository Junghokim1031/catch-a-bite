package com.deliveryapp.catchabite.common.constant;

public final class PasswordPolicyConstant {
    public static final String PASSWORD_REGEX =
        "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=[\\]{};':\"\\\\|,.<>/?]).{8,}$";
    public static final String PASSWORD_MESSAGE =
        "비밀번호는 8자 이상이며, 영문·숫자·특수문자를 포함해야 합니다.";

    private PasswordPolicyConstant() {
    }
}
