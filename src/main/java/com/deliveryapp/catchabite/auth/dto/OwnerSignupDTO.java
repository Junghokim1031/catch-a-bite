package com.deliveryapp.catchabite.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OwnerSignupDTO {

	@NotBlank(message = "loginId is required")
	private String loginId;

	@NotBlank(message = "password is required")
	private String password;

	@NotBlank(message = "mobile is required")
	@Pattern(regexp = "^[0-9]{11}$")
	private String mobile;

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
