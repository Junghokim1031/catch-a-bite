package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자(AppUser) 조회 및 중복 검증용 JPA 레포지토리
 */
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    // 이메일로 사용자 조회
    Optional<AppUser> findByAppUserEmail(String appUserEmail);

    // 이메일 또는 휴대폰으로 사용자 조회 (로그인용)
    Optional<AppUser> findByAppUserEmailOrAppUserMobile(String appUserEmail, String appUserMobile);

    // 닉네임으로 사용자 조회
    Optional<AppUser> findByAppUserNickname(String appUserNickname);

    // 이메일 중복 여부 확인
    boolean existsByAppUserEmail(String appUserEmail);

    // 휴대폰 번호 중복 여부 확인
    boolean existsByAppUserMobile(String appUserMobile);

    // 닉네임 중복 여부 확인
    boolean existsByAppUserNickname(String appUserNickname);
}
