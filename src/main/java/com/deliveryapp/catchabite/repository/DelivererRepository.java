package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.Deliverer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 라이더(Deliverer) 조회 및 중복 검사용 JPA 레포지토리
 */
public interface DelivererRepository extends JpaRepository<Deliverer, Long> {

    // 이메일로 라이더 조회
    Optional<Deliverer> findByDelivererEmail(String delivererEmail);

    // 이메일 중복 여부 확인
    boolean existsByDelivererEmail(String delivererEmail);

    //운전면허 중복 여부 확인(오토바이/자동차만 사용)
    boolean existsByDelivererLicenseNumber(String delivererLicenseNumber);

    // 차량번호 중복 여부 확인(오토바이/자동차만 사용)
    boolean existsByDelivererVehicleNumber(String delivererVehicleNumber);
}
