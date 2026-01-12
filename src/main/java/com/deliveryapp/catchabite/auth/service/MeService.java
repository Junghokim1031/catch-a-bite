package com.deliveryapp.catchabite.auth.service;

import com.deliveryapp.catchabite.auth.domain.Account;
import com.deliveryapp.catchabite.auth.domain.AccountStatus;
import com.deliveryapp.catchabite.auth.dto.AccountDTO;
import com.deliveryapp.catchabite.auth.dto.ProfileUpdateDTO;
import com.deliveryapp.catchabite.auth.port.AccountStorePort;
import com.deliveryapp.catchabite.common.exception.AppException;
import com.deliveryapp.catchabite.common.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
public class MeService {

	private final AccountStorePort accountStorePort;

	public MeService(AccountStorePort accountStorePort) {
		this.accountStorePort = accountStorePort;
	}

	public AccountDTO getMe(String loginId) {
		System.out.println("MeService - getMe");

		Account account = accountStorePort.findByLoginId(loginId)
			.orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

		validateActive(account);
		return AccountDTO.from(account);
	}

	public AccountDTO updateMe(String loginId, ProfileUpdateDTO profileUpdateDTO) {
		System.out.println("MeService - updateMe");

		Account account = accountStorePort.findByLoginId(loginId)
			.orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

		validateActive(account);

		// 닉네임 중복(본인 제외 로직은 DB 붙인 후 강화 가능)
		if (accountStorePort.existsNickname(profileUpdateDTO.getNickname())
			&& (account.getNickname() == null || !account.getNickname().equals(profileUpdateDTO.getNickname()))
		) {
			throw new AppException(ErrorCode.DUPLICATE_NICKNAME);
		}

		// 모바일 중복(본인 제외)
		if (accountStorePort.existsMobile(profileUpdateDTO.getMobile())
			&& (account.getMobile() == null || !account.getMobile().equals(profileUpdateDTO.getMobile()))
		) {
			throw new AppException(ErrorCode.DUPLICATE_MOBILE);
		}

		Account updated = accountStorePort.updateProfile(loginId, profileUpdateDTO.getNickname(), profileUpdateDTO.getMobile());
		return AccountDTO.from(updated);
	}

	private void validateActive(Account account) {
		if (account.getAccountStatus() == AccountStatus.SUSPENDED) {
			throw new AppException(ErrorCode.ACCOUNT_SUSPENDED);
		}
		if (account.getAccountStatus() == AccountStatus.WITHDRAWN) {
			throw new AppException(ErrorCode.ACCOUNT_WITHDRAWN);
		}
	}
}
