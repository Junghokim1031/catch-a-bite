package com.deliveryapp.catchabite.auth.service;

import com.deliveryapp.catchabite.repository.DelivererRepository;
import org.springframework.stereotype.Service;

/**
 * 라이더 중복 체크 서비스
 */
@Service
public class DelivererAuthService {

    private final DelivererRepository delivererRepository;

    public DelivererAuthService(DelivererRepository delivererRepository) {
        this.delivererRepository = delivererRepository;
    }

    public boolean existsEmail(String email) {
        return delivererRepository.existsByDelivererEmail(email);
    }

    public boolean existsMobile(String mobile) {
        return delivererRepository.existsByDelivererMobile(mobile);
    }
}
