package com.deliveryapp.catchabite.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ProfileUpdateDTO {

	@NotBlank(message = "nickname is required")
	private String nickname;

	@NotBlank(message = "mobile is required")
	@Pattern(regexp = "^[0-9]{11}$")
	private String mobile;

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
