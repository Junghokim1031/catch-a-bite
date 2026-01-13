package com.deliveryapp.catchabite.auth.service;

import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 내 정보 조회 비즈니스 로직
 * 사용자 단건 조회 담당
 */
@Service
@RequiredArgsConstructor
public class MeService {

    // 회원 DB 조회 레포지토리
    private final AppUserRepository appUserRepository;

    // appUserId 기준 사용자 조회
    public AppUser getMe(Long appUserId) {
        return appUserRepository.findById(appUserId)
            .orElseThrow(() -> new RuntimeException("사용자 없음"));
    }
}
