package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    // 로그인 ID로 사용자 조회
    Optional<AppUser> findByAppUserLoginId(String appUserLoginId);

    // 회원가입 시 중복 체크용
    boolean existsByAppUserLoginId(String appUserLoginId);
}
