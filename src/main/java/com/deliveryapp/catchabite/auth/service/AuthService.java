package com.deliveryapp.catchabite.auth.service;

import com.deliveryapp.catchabite.auth.domain.Account;
import com.deliveryapp.catchabite.auth.domain.AccountStatus;
import com.deliveryapp.catchabite.auth.dto.OwnerSignupDTO;
import com.deliveryapp.catchabite.auth.dto.RiderSignupDTO;
import com.deliveryapp.catchabite.auth.dto.UserSignupDTO;
import com.deliveryapp.catchabite.auth.port.AccountStorePort;
import com.deliveryapp.catchabite.common.constant.RoleConstant;
import com.deliveryapp.catchabite.common.exception.AppException;
import com.deliveryapp.catchabite.common.exception.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

	private final AccountStorePort accountStorePort;
	private final PasswordEncoder passwordEncoder;

	public AuthService(AccountStorePort accountStorePort, PasswordEncoder passwordEncoder) {
		this.accountStorePort = accountStorePort;
		this.passwordEncoder = passwordEncoder;
	}

	public void createUserAccount(UserSignupDTO userSignupDTO) {
		System.out.println("AuthService - createUserAccount");

		validateCommonSignup(userSignupDTO.getLoginId(), userSignupDTO.getPassword(), userSignupDTO.getMobile());
		if (accountStorePort.existsNickname(userSignupDTO.getNickname())) {
			throw new AppException(ErrorCode.DUPLICATE_NICKNAME);
		}

		String encodedPassword = passwordEncoder.encode(userSignupDTO.getPassword());

		Account account = new Account(
			null,
			userSignupDTO.getLoginId(),
			encodedPassword,
			RoleConstant.ROLE_USER,
			userSignupDTO.getNickname(),
			userSignupDTO.getMobile(),
			userSignupDTO.getName(),         // ✅ 추가
			AccountStatus.ACTIVE,
			LocalDateTime.now()
		);

		accountStorePort.saveAccount(account);
	}

	public void createOwnerAccount(OwnerSignupDTO ownerSignupDTO) {
		System.out.println("AuthService - createOwnerAccount");

		validateCommonSignup(ownerSignupDTO.getLoginId(), ownerSignupDTO.getPassword(), ownerSignupDTO.getMobile());

		String encodedPassword = passwordEncoder.encode(ownerSignupDTO.getPassword());

		Account account = new Account(
			null,
			ownerSignupDTO.getLoginId(),
			encodedPassword,
			RoleConstant.ROLE_OWNER,
			null,
			ownerSignupDTO.getMobile(),
			null,                           // ✅ name은 추후 OwnerSignupDTO에 추가되면 넣기
			AccountStatus.ACTIVE,
			LocalDateTime.now()
		);

		accountStorePort.saveAccount(account);
	}

	public void createRiderAccount(RiderSignupDTO riderSignupDTO) {
		System.out.println("AuthService - createRiderAccount");

		validateCommonSignup(riderSignupDTO.getLoginId(), riderSignupDTO.getPassword(), riderSignupDTO.getMobile());

		String encodedPassword = passwordEncoder.encode(riderSignupDTO.getPassword());

		Account account = new Account(
			null,
			riderSignupDTO.getLoginId(),
			encodedPassword,
			RoleConstant.ROLE_RIDER,
			null,
			riderSignupDTO.getMobile(),
			null,                           // ✅ name
			AccountStatus.ACTIVE,
			LocalDateTime.now()
		);

		accountStorePort.saveAccount(account);
	}

	private void validateCommonSignup(String loginId, String password, String mobile) {
		if (accountStorePort.existsLoginId(loginId)) {
			throw new AppException(ErrorCode.DUPLICATE_LOGIN_ID);
		}
		if (accountStorePort.existsMobile(mobile)) {
			throw new AppException(ErrorCode.DUPLICATE_MOBILE);
		}

		// ✅ 지금은 현실적으로 8~12 추천 (16은 너무 강함)
		if (password.length() < 8) {
			throw new AppException(ErrorCode.INVALID_REQUEST, "password is too short");
		}
	}
}
