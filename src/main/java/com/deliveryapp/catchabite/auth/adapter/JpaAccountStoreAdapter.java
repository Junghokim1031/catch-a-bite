package com.deliveryapp.catchabite.auth.adapter;

import com.deliveryapp.catchabite.auth.domain.Account;
import com.deliveryapp.catchabite.auth.domain.AccountStatus;
import com.deliveryapp.catchabite.auth.port.AccountStorePort;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.user.repository.AppUserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Profile("jpa") // ✅ 이 프로필 켜면 JPA로 동작 (예: --spring.profiles.active=jpa)
public class JpaAccountStoreAdapter implements AccountStorePort {

	private static final LocalDate DEFAULT_BIRTHDAY = LocalDate.of(1900, 1, 1);
	private static final String DEFAULT_GENDER = "U";
	private static final String ACTIVE_STATUS = "1";

	private final AppUserRepository appUserRepository;

	public JpaAccountStoreAdapter(AppUserRepository appUserRepository) {
		this.appUserRepository = appUserRepository;
	}

	@Override
	public boolean existsLoginId(String loginId) {
		// loginId = email 사용
		return appUserRepository.existsByAppUserEmail(loginId);
	}

	@Override
	public boolean existsNickname(String nickname) {
		return appUserRepository.existsByAppUserNickname(nickname);
	}

	@Override
	public boolean existsMobile(String mobile) {
		return appUserRepository.existsByAppUserMobile(mobile);
	}

	@Override
public Account saveAccount(Account account) {
	System.out.println("JpaAccountStoreAdapter - saveAccount (DB INSERT)");
	AppUser appUser = AppUser.builder()
		.appUserNickname(account.getNickname() == null ? account.getLoginId() : account.getNickname())
		.appUserPassword(account.getEncodedPassword())
		.appUserName(account.getName() == null ? account.getLoginId() : account.getName())
		.appUserBirthday(DEFAULT_BIRTHDAY)
		.appUserGender(DEFAULT_GENDER)
		.appUserMobile(account.getMobile())
		.appUserEmail(account.getLoginId())
		.appUserCreatedDate(LocalDate.now())
		.appUserStatus(ACTIVE_STATUS)
		.build();

	AppUser saved = appUserRepository.save(appUser);

	return new Account(
		saved.getAppUserId(),
		saved.getAppUserEmail(),
		saved.getAppUserPassword(),
		account.getRoleName(),
		saved.getAppUserNickname(),
		saved.getAppUserMobile(),
		saved.getAppUserName(),
		AccountStatus.ACTIVE,
		LocalDateTime.now()
	);
}

@Override
public Optional<Account> findByLoginId(String loginId) {
	return appUserRepository.findByAppUserEmail(loginId)
		.map(appUser -> new Account(
			appUser.getAppUserId(),
			appUser.getAppUserEmail(),
			appUser.getAppUserPassword(),
			"ROLE_USER",
			appUser.getAppUserNickname(),
			appUser.getAppUserMobile(),
			appUser.getAppUserName(),
			AccountStatus.ACTIVE,
			LocalDateTime.now()
		));
}

@Override
public Account updateProfile(String loginId, String nickname, String mobile) {
	AppUser appUser = appUserRepository.findByAppUserEmail(loginId).orElse(null);
	if (appUser == null) {
		return null;
	}
	AppUser saved = appUserRepository.save(
		baseBuilder(appUser)
			.appUserNickname(nickname)
			.appUserMobile(mobile)
			.build()
	);

	return new Account(
		saved.getAppUserId(),
		saved.getAppUserEmail(),
		saved.getAppUserPassword(),
		"ROLE_USER",
		saved.getAppUserNickname(),
		saved.getAppUserMobile(),
		saved.getAppUserName(),
		AccountStatus.ACTIVE,
		LocalDateTime.now()
	);
}

@Override
public Account updatePassword(String loginId, String encodedPassword) {
	AppUser appUser = appUserRepository.findByAppUserEmail(loginId).orElse(null);
	if (appUser == null) {
		return null;
	}
	AppUser saved = appUserRepository.save(
		baseBuilder(appUser)
			.appUserPassword(encodedPassword)
			.build()
	);

	return new Account(
		saved.getAppUserId(),
		saved.getAppUserEmail(),
		saved.getAppUserPassword(),
		"ROLE_USER",
		saved.getAppUserNickname(),
		saved.getAppUserMobile(),
		saved.getAppUserName(),
		AccountStatus.ACTIVE,
		LocalDateTime.now()
	);
}

@Override
public Account updateStatus(String loginId, String status) {
	AppUser appUser = appUserRepository.findByAppUserEmail(loginId).orElse(null);
	if (appUser == null) {
		return null;
	}
	AppUser saved = appUserRepository.save(
		baseBuilder(appUser)
			.appUserStatus(status)
			.build()
	);

	return new Account(
		saved.getAppUserId(),
		saved.getAppUserEmail(),
		saved.getAppUserPassword(),
		"ROLE_USER",
		saved.getAppUserNickname(),
		saved.getAppUserMobile(),
		saved.getAppUserName(),
		AccountStatus.ACTIVE,
		LocalDateTime.now()
	);
}

private AppUser.AppUserBuilder baseBuilder(AppUser appUser) {
	return AppUser.builder()
		.appUserId(appUser.getAppUserId())
		.appUserNickname(appUser.getAppUserNickname())
		.appUserPassword(appUser.getAppUserPassword())
		.appUserName(appUser.getAppUserName())
		.appUserBirthday(appUser.getAppUserBirthday())
		.appUserGender(appUser.getAppUserGender())
		.appUserMobile(appUser.getAppUserMobile())
		.appUserEmail(appUser.getAppUserEmail())
		.appUserCreatedDate(appUser.getAppUserCreatedDate())
		.appUserStatus(appUser.getAppUserStatus())
		.addresses(appUser.getAddresses())
		.favoriteStores(appUser.getFavoriteStores())
		.carts(appUser.getCarts())
		.orders(appUser.getOrders())
		.reviews(appUser.getReviews())
		.notifications(appUser.getNotifications());
}
}
