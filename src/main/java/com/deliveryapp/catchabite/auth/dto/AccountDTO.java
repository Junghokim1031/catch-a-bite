package com.deliveryapp.catchabite.auth.dto;

import com.deliveryapp.catchabite.auth.domain.Account;
import com.deliveryapp.catchabite.auth.domain.AccountStatus;

import java.time.LocalDateTime;

public class AccountDTO {

	private Long accountId;
	private String loginId;
	private String roleName;
	private String nickname;
	private String mobile;
	private AccountStatus accountStatus;
	private LocalDateTime createdDate;

	public static AccountDTO from(Account account) {
		AccountDTO dto = new AccountDTO();
		dto.accountId = account.getAccountId();
		dto.loginId = account.getLoginId();
		dto.roleName = account.getRoleName();
		dto.nickname = account.getNickname();
		dto.mobile = account.getMobile();
		dto.accountStatus = account.getAccountStatus();
		dto.createdDate = account.getCreatedDate();
		return dto;
	}

	public Long getAccountId() {
		return accountId;
	}

	public String getLoginId() {
		return loginId;
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

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}
}
