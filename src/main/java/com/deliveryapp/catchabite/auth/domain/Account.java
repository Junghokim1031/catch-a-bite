package com.deliveryapp.catchabite.auth.domain;

import java.time.LocalDateTime;

public class Account {

	private Long accountId;
	private String loginId;
	private String encodedPassword;
	private String roleName;
	private String nickname;
	private String mobile;
	private String name; // ✅ 추가
	private AccountStatus accountStatus;
	private LocalDateTime createdDate;

	public Account(
		Long accountId,
		String loginId,
		String encodedPassword,
		String roleName,
		String nickname,
		String mobile,
		String name,                 // ✅ 추가
		AccountStatus accountStatus,
		LocalDateTime createdDate
	) {
		this.accountId = accountId;
		this.loginId = loginId;
		this.encodedPassword = encodedPassword;
		this.roleName = roleName;
		this.nickname = nickname;
		this.mobile = mobile;
		this.name = name;           // ✅ 추가
		this.accountStatus = accountStatus;
		this.createdDate = createdDate;
	}

	public Long getAccountId() {
		return accountId;
	}

	public String getLoginId() {
		return loginId;
	}

	public String getEncodedPassword() {
		return encodedPassword;
	}

	public String getRoleName() {
		return roleName;
	}

	public String getNickname() {
		return nickname;
	}

	public String getMobile() {
		return mobile;
	}

	public String getName() {      // ✅ 추가
		return name;
	}

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setEncodedPassword(String encodedPassword) {
		this.encodedPassword = encodedPassword;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public void setName(String name) { // ✅ 추가(프로필 수정 대비)
		this.name = name;
	}
}
