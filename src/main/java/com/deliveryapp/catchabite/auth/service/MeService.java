package com.deliveryapp.catchabite.auth.service;

import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * - 컨트롤러에서 appUserId(또는 loginId) 받아서 조회하는 방식
 */
@Service
@RequiredArgsConstructor
public class MeService {

    private final AppUserRepository appUserRepository;

    public AppUser getMe(Long appUserId) {
        return appUserRepository.findById(appUserId)
            .orElseThrow(() -> new RuntimeException("사용자 없음"));
    }
}