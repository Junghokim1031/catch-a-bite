package com.deliveryapp.catchabite.auth.application;

import com.deliveryapp.catchabite.auth.api.dto.*;
import com.deliveryapp.catchabite.common.constant.RoleConstant;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.repository.AppUserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 인증/회원가입 비즈니스 로직 구현체
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    // 사용자 저장소 및 비밀번호 암호화 도구 주입
    public AuthServiceImpl(
        AppUserRepository appUserRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입 처리 및 사용자 계정 생성
    @Override
    public SignUpResponse signUp(SignUpRequest request) {

        // 필수 약관 미동의 시 가입 불가
        if (!request.requiredTermsAccepted()) {
            throw new IllegalArgumentException("Required terms must be accepted.");
        }

        // 비밀번호 확인 불일치 검증
        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Password confirmation does not match.");
        }

        // 이메일/휴대폰/닉네임 중복 검증
        if (appUserRepository.existsByAppUserEmail(request.loginId())) {
            throw new IllegalArgumentException("Email is already in use.");
        }
        if (appUserRepository.existsByAppUserMobile(request.mobile())) {
            throw new IllegalArgumentException("Mobile number is already in use.");
        }
        if (appUserRepository.existsByAppUserNickname(request.nickname())) {
            throw new IllegalArgumentException("Nickname is already in use.");
        }

        // 사용자 계정 저장
        AppUser saved = appUserRepository.save(
            AppUser.builder()
                .appUserEmail(request.loginId())
                .appUserPassword(passwordEncoder.encode(request.password()))
                .appUserNickname(request.nickname())
                .appUserMobile(request.mobile())
                .appUserName(request.name())
                .appUserCreatedDate(LocalDateTime.now())
                .build()
        );

        // 회원가입 결과 반환
        return new SignUpResponse(
            saved.getAppUserId(),
            saved.getAppUserEmail(),
            saved.getAppUserNickname(),
            RoleConstant.ROLE_USER
        );
    }

    // 로그인 처리 및 사용자 인증
    @Override
    public LoginResponse login(LoginRequest request) {

        AppUser account = appUserRepository
            .findByAppUserEmailOrAppUserMobile(request.loginKey(), request.loginKey())
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));

        // 비밀번호 일치 여부 검증
        if (!passwordEncoder.matches(request.password(), account.getAppUserPassword())) {
            throw new IllegalArgumentException("Invalid credentials.");
        }

        return new LoginResponse(
            account.getAppUserId(),
            account.getAppUserNickname(),
            RoleConstant.ROLE_USER
        );
    }

    // 이메일 중복 여부 확인
    @Override
    public boolean existsLoginId(String loginId) {
        return appUserRepository.existsByAppUserEmail(loginId);
    }

    // 휴대폰 번호 중복 여부 확인
    @Override
    public boolean existsMobile(String mobile) {
        return appUserRepository.existsByAppUserMobile(mobile);
    }

    // 닉네임 중복 여부 확인
    @Override
    public boolean existsNickname(String nickname) {
        return appUserRepository.existsByAppUserNickname(nickname);
    }
}
