package com.deliveryapp.catchabite.auth.api;

import com.deliveryapp.catchabite.auth.api.dto.DelivererLoginRequest;
import com.deliveryapp.catchabite.auth.api.dto.DelivererSignUpRequest;
import com.deliveryapp.catchabite.domain.enumtype.DelivererVehicleType;
import com.deliveryapp.catchabite.domain.enumtype.YesNo;
import com.deliveryapp.catchabite.entity.Deliverer;
import com.deliveryapp.catchabite.repository.DelivererRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/** 라이더 인증 API 컨트롤러 */
@RestController
@RequestMapping("/api/v1/deliverer/auth")
public class DelivererAuthController {

    private final DelivererRepository delivererRepository;
    private final PasswordEncoder passwordEncoder;

    public DelivererAuthController(DelivererRepository delivererRepository, PasswordEncoder passwordEncoder) {
        this.delivererRepository = delivererRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 라이더 회원가입 API
    @PostMapping("/signup")
    public String signup(@Valid @RequestBody DelivererSignUpRequest request) {

        if (delivererRepository.existsByDelivererEmail(request.email())) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        DelivererVehicleType type = DelivererVehicleType.valueOf(request.vehicleType());

        boolean needsVehicleInfo = type == DelivererVehicleType.MOTORBIKE || type == DelivererVehicleType.CAR;
        if (needsVehicleInfo && (isBlank(request.licenseNumber()) || isBlank(request.vehicleNumber()))) {
            throw new IllegalArgumentException("Vehicle info is required.");
        }

        if (needsVehicleInfo && delivererRepository.existsByDelivererLicenseNumber(request.licenseNumber())) {
            throw new IllegalArgumentException("License number is already in use.");
        }
        if (needsVehicleInfo && delivererRepository.existsByDelivererVehicleNumber(request.vehicleNumber())) {
            throw new IllegalArgumentException("Vehicle number is already in use.");
        }

        delivererRepository.save(Deliverer.builder()
            .delivererEmail(request.email())
            .delivererPassword(passwordEncoder.encode(request.password()))
            .delivererVehicleType(type)
            .delivererLicenseNumber(needsVehicleInfo ? request.licenseNumber() : null)
            .delivererVehicleNumber(needsVehicleInfo ? request.vehicleNumber() : null)
            .delivererVerified(YesNo.N)
            .delivererCreatedDate(LocalDateTime.now())
            .build());

        return "ok";
    }

    // 라이더 로그인 API
    @PostMapping("/login")
    public String login(@Valid @RequestBody DelivererLoginRequest request) {

        Deliverer deliverer = delivererRepository.findByDelivererEmail(request.email())
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));

        if (!passwordEncoder.matches(request.password(), deliverer.getDelivererPassword())) {
            throw new IllegalArgumentException("Invalid credentials.");
        }

        return "ok";
    }

    private boolean isBlank(String v) {
        return v == null || v.isBlank();
    }
}
