package com.deliveryapp.catchabite.user.repository;

import com.deliveryapp.catchabite.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

	boolean existsByAppUserEmail(String appUserEmail);

	boolean existsByAppUserNickname(String appUserNickname);

	boolean existsByAppUserMobile(String appUserMobile);

	Optional<AppUser> findByAppUserEmail(String appUserEmail);
}
