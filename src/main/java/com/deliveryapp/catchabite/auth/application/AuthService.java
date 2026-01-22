package com.deliveryapp.catchabite.auth.application;

import com.deliveryapp.catchabite.auth.api.dto.*;

/**
 * 인증/회원 관련 비즈니스 로직 인터페이스
 */
public interface AuthService {

    // 회원가입 처리
    SignUpResponse signUp(SignUpRequest request);

    // 로그인 처리
    LoginResponse login(LoginRequest request);

    // 로그인 ID(이메일) 중복 여부 확인
    boolean existsLoginId(String loginId);

    // 휴대폰 번호 중복 여부 확인
    boolean existsMobile(String mobile);

    // 닉네임 중복 여부 확인
    boolean existsNickname(String nickname);

    // 로그인 사용자 정보 조회
    MeResponse getMe();
}
