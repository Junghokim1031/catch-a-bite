package com.deliveryapp.catchabite.auth.service;

import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * - 회원가입: 비밀번호 BCrypt 인코딩 후 저장
 * - 로그인: loginId로 조회 + password matches
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUser signup(AppUser appUser) {
        String encoded = passwordEncoder.encode(appUser.getAppUserPassword());
        appUser.changePassword(encoded);
        return appUserRepository.save(appUser);
    }

    public AppUser login(String loginId, String password) {

        AppUser user = appUserRepository.findByAppUserLoginId(loginId)
            .orElseThrow(() -> new RuntimeException("로그인 실패"));

        if (!passwordEncoder.matches(password, user.getAppUserPassword())) {
            throw new RuntimeException("로그인 실패");
        }

        return user;
    }
}
