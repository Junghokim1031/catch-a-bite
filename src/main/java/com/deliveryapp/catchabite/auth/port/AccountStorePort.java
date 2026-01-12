package com.deliveryapp.catchabite.auth.port;

import com.deliveryapp.catchabite.auth.domain.Account;

import java.util.Optional;

public interface AccountStorePort {

	boolean existsLoginId(String loginId);

	boolean existsNickname(String nickname);

	boolean existsMobile(String mobile);

	Account saveAccount(Account account);

	Optional<Account> findByLoginId(String loginId);

	Account updateProfile(String loginId, String nickname, String mobile);

	Account updatePassword(String loginId, String encodedPassword);

	Account updateStatus(String loginId, String status);
}
