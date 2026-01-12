package com.deliveryapp.catchabite.common.exception;

public enum ErrorCode {

	INVALID_REQUEST("INVALID_REQUEST", "invalid request"),
	DUPLICATE_LOGIN_ID("DUPLICATE_LOGIN_ID", "duplicate login id"),
	DUPLICATE_NICKNAME("DUPLICATE_NICKNAME", "duplicate nickname"),
	DUPLICATE_MOBILE("DUPLICATE_MOBILE", "duplicate mobile"),
	ACCOUNT_NOT_FOUND("ACCOUNT_NOT_FOUND", "account not found"),
	ACCOUNT_SUSPENDED("ACCOUNT_SUSPENDED", "account suspended"),
	ACCOUNT_WITHDRAWN("ACCOUNT_WITHDRAWN", "account withdrawn"),
	FORBIDDEN("FORBIDDEN", "forbidden");

	private final String code;
	private final String message;

	ErrorCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
