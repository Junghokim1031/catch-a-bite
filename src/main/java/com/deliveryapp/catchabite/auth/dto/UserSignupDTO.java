package com.deliveryapp.catchabite.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserSignupDTO {

	@NotBlank
	@Size(max = 100)
	private String name;

	@NotBlank
	private String loginId; // email

	@NotBlank
	@Size(min = 8)
	private String password;

	@NotBlank
	@Size(max = 50)
	private String nickname;

	@NotBlank
	@Pattern(regexp = "^[0-9]{11}$")
	private String mobile;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
