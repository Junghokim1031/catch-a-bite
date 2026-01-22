package com.deliveryapp.catchabite.auth.service;

import com.deliveryapp.catchabite.repository.StoreOwnerRepository;
import org.springframework.stereotype.Service;

/**
 * 사장님 중복 체크 서비스
 */
@Service
public class StoreOwnerAuthService {

    private final StoreOwnerRepository storeOwnerRepository;

    public StoreOwnerAuthService(StoreOwnerRepository storeOwnerRepository) {
        this.storeOwnerRepository = storeOwnerRepository;
    }

    public boolean existsEmail(String email) {
        return storeOwnerRepository.existsByStoreOwnerEmail(email);
    }

    public boolean existsMobile(String mobile) {
        return storeOwnerRepository.existsByStoreOwnerMobile(mobile);
    }
}
