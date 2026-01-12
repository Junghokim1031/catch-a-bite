package com.deliveryapp.catchabite.auth.adapter;

import com.deliveryapp.catchabite.auth.domain.Account;
import com.deliveryapp.catchabite.auth.domain.AccountStatus;
import com.deliveryapp.catchabite.auth.port.AccountStorePort;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Profile("local") // ✅ 이 프로필 켜면 In-Memory로 동작 (예: --spring.profiles.active=local)
public class InMemoryAccountStoreAdapter implements AccountStorePort {

	private final AtomicLong accountIdSeq = new AtomicLong(0);
	private final ConcurrentHashMap<String, Account> accountMap = new ConcurrentHashMap<>();

	@Override
	public boolean existsLoginId(String loginId) {
		return accountMap.containsKey(loginId);
	}

	@Override
	public boolean existsNickname(String nickname) {
		return accountMap.values().stream()
			.anyMatch(account -> account.getNickname() != null && account.getNickname().equals(nickname));
	}

	@Override
	public boolean existsMobile(String mobile) {
		return accountMap.values().stream()
			.anyMatch(account -> account.getMobile() != null && account.getMobile().equals(mobile));
	}

	@Override
	public Account saveAccount(Account account) {
		System.out.println("InMemoryAccountStoreAdapter - saveAccount (MEMORY)");
		Long newId = accountIdSeq.incrementAndGet();

		Account savedAccount = new Account(
			newId,
            account.getName(),
			account.getLoginId(),
			account.getEncodedPassword(),
			account.getRoleName(),
			account.getNickname(),
			account.getMobile(),
			AccountStatus.ACTIVE,
			LocalDateTime.now()
		);

		accountMap.put(savedAccount.getLoginId(), savedAccount);
		return savedAccount;
	}

	@Override
	public Optional<Account> findByLoginId(String loginId) {
		return Optional.ofNullable(accountMap.get(loginId));
	}

	@Override
	public Account updateProfile(String loginId, String nickname, String mobile) {
		Account account = accountMap.get(loginId);
		if (account == null) {
			return null;
		}
		account.setNickname(nickname);
		account.setMobile(mobile);
		return account;
	}

	@Override
	public Account updatePassword(String loginId, String encodedPassword) {
		Account account = accountMap.get(loginId);
		if (account == null) {
			return null;
		}
		account.setEncodedPassword(encodedPassword);
		return account;
	}

	@Override
	public Account updateStatus(String loginId, String status) {
		Account account = accountMap.get(loginId);
		if (account == null) {
			return null;
		}
		account.setAccountStatus(AccountStatus.valueOf(status));
		return account;
	}
}
