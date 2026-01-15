package com.deliveryapp.catchabite.auth.controller;

import com.deliveryapp.catchabite.auth.AuthSessionKeys;
import com.deliveryapp.catchabite.auth.api.dto.LoginRequest;
import com.deliveryapp.catchabite.auth.api.dto.LoginResponse;
import com.deliveryapp.catchabite.auth.api.dto.SignUpRequest;
import com.deliveryapp.catchabite.auth.application.AuthService;
import com.deliveryapp.catchabite.domain.enumtype.DelivererVehicleType;
import com.deliveryapp.catchabite.domain.enumtype.YesNo;
import com.deliveryapp.catchabite.entity.Deliverer;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.entity.StoreOwner;
import com.deliveryapp.catchabite.repository.DelivererRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;
import com.deliveryapp.catchabite.repository.StoreOwnerRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 폼 기반 회원가입/로그인 처리 컨트롤러 (사용자/사장/라이더)
 */
@Controller
@RequestMapping("/auth")
public class AuthFormController {

    private final AuthService authService;
    private final StoreOwnerRepository storeOwnerRepository;
    private final StoreRepository storeRepository;
    private final DelivererRepository delivererRepository;
    private final PasswordEncoder passwordEncoder;

    // 폼 인증 처리에 필요한 서비스/레포지토리/인코더 주입
    public AuthFormController(
        AuthService authService,
        StoreOwnerRepository storeOwnerRepository,
        StoreRepository storeRepository,
        DelivererRepository delivererRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.authService = authService;
        this.storeOwnerRepository = storeOwnerRepository;
        this.storeRepository = storeRepository;
        this.delivererRepository = delivererRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 사용자 회원가입 폼 처리
    @PostMapping("/user/signup")
    public String userSignup(@Valid SignUpRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/auth/user/signup?error";
        }
        try {
            authService.signUp(request);
            return "redirect:/auth/user/signup?signup";
        } catch (RuntimeException ex) {
            return "redirect:/auth/user/signup?error";
        }
    }

    // 사용자 로그인 폼 처리
    @PostMapping("/user/login")
    public String userLogin(@Valid LoginRequest request, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "redirect:/auth/user/signup?error";
        }
        try {
            LoginResponse loginResponse = authService.login(request);
            session.setAttribute(AuthSessionKeys.LOGIN_USER_ID, loginResponse.accountId());
            return "redirect:/auth/user/signup?loginSuccess";
        } catch (RuntimeException ex) {
            return "redirect:/auth/user/signup?error";
        }
    }

    // 사장 회원가입 폼 처리
    @PostMapping("/owner/signup")
    @Transactional
    public String ownerSignup(
        @RequestParam String email,
        @RequestParam String password,
        @RequestParam String name,
        @RequestParam String mobile,
        @RequestParam String businessRegistrationNo,
        @RequestParam String storeName,
        @RequestParam String storeAddress
    ) {
        if (isBlank(email) || isBlank(password) || isBlank(name)
            || isBlank(mobile) || isBlank(businessRegistrationNo)
            || isBlank(storeName) || isBlank(storeAddress)) {
            return "redirect:/auth/owner/signup?error";
        }
        if (storeOwnerRepository.existsByStoreOwnerEmail(email)
            || storeOwnerRepository.existsByStoreOwnerMobile(mobile)
            || storeOwnerRepository.existsByStoreOwnerBusinessRegistrationNo(businessRegistrationNo)) {
            return "redirect:/auth/owner/signup?error";
        }

        StoreOwner owner = storeOwnerRepository.save(StoreOwner.builder()
            .storeOwnerEmail(email)
            .storeOwnerPassword(passwordEncoder.encode(password))
            .storeOwnerName(name)
            .storeOwnerMobile(mobile)
            .storeOwnerBusinessRegistrationNo(businessRegistrationNo)
            .build());

        Store store = Store.builder()
            .storeOwner(owner)
            .storeOwnerName(name)
            .storeName(storeName)
            .storeAddress(storeAddress)
            .storeCategory("UNASSIGNED")
            .storePhone(normalizeStorePhone(mobile))
            .build();

        storeRepository.save(store);
        return "redirect:/auth/owner/signup?signup";
    }

    // 사장 로그인 폼 처리
    @PostMapping("/owner/login")
    public String ownerLogin(
        @RequestParam String email,
        @RequestParam String password,
        HttpSession session
    ) {
        if (isBlank(email) || isBlank(password)) {
            return "redirect:/auth/owner/signup?error";
        }
        StoreOwner owner = storeOwnerRepository.findByStoreOwnerEmail(email).orElse(null);
        if (owner == null || !passwordEncoder.matches(password, owner.getStoreOwnerPassword())) {
            return "redirect:/auth/owner/signup?error";
        }

        session.setAttribute(AuthSessionKeys.LOGIN_OWNER_ID, owner.getStoreOwnerId());
        return "redirect:/auth/owner/signup?loginSuccess";
    }

    // 라이더 회원가입 폼 처리
@PostMapping("/rider/signup")
public String riderSignup(
    @RequestParam String loginId,
    @RequestParam String password,
    @RequestParam String confirmPassword,
    @RequestParam(required = false) String vehicleType,
    @RequestParam(required = false) String licenseNumber,
    @RequestParam(required = false) String vehicleNumber
) {
    if (isBlank(loginId) || isBlank(password) || isBlank(confirmPassword)) {
        return "redirect:/auth/rider/signup?error";
    }
    if (!password.equals(confirmPassword)) {
        return "redirect:/auth/rider/signup?error";
    }
    if (delivererRepository.existsByDelivererEmail(loginId)) {
        return "redirect:/auth/rider/signup?error";
    }

    DelivererVehicleType parsedType = parseVehicleType(vehicleType);
    if (parsedType == null) {
        return "redirect:/auth/rider/signup?error";
    }

    boolean needsVehicleInfo = parsedType == DelivererVehicleType.MOTORBIKE
        || parsedType == DelivererVehicleType.CAR;

    // 오토바이/자동차만 면허/차량번호 필수
    if (needsVehicleInfo && (isBlank(licenseNumber) || isBlank(vehicleNumber))) {
        return "redirect:/auth/rider/signup?error";
    }

    // 오토바이/자동차만 중복 체크(입력된 경우만)
    if (needsVehicleInfo) {
        if (delivererRepository.existsByDelivererLicenseNumber(licenseNumber)) {
            return "redirect:/auth/rider/signup?error";
        }
        if (delivererRepository.existsByDelivererVehicleNumber(vehicleNumber)) {
            return "redirect:/auth/rider/signup?error";
        }
    }

    Deliverer deliverer = Deliverer.builder()
        .delivererEmail(loginId)
        .delivererPassword(passwordEncoder.encode(password))
        .delivererVehicleType(parsedType)
        .delivererLicenseNumber(needsVehicleInfo ? licenseNumber : null)
        .delivererVehicleNumber(needsVehicleInfo ? vehicleNumber : null)
        .delivererVerified(YesNo.N)
        .delivererCreatedDate(java.time.LocalDateTime.now())
        .build();

    delivererRepository.save(deliverer);
    return "redirect:/auth/rider/signup?signup";
}
    // 라이더 로그인 폼 처리
    @PostMapping("/rider/login")
    public String riderLogin(
        @RequestParam String loginKey,
        @RequestParam String password,
        HttpSession session
    ) {
        if (isBlank(loginKey) || isBlank(password)) {
            return "redirect:/auth/rider/signup?error";
        }

        Deliverer rider = delivererRepository.findByDelivererEmail(loginKey).orElse(null);
        if (rider == null || !passwordEncoder.matches(password, rider.getDelivererPassword())) {
            return "redirect:/auth/rider/signup?error";
        }

        session.setAttribute(AuthSessionKeys.LOGIN_RIDER_ID, rider.getDelivererId());
        return "redirect:/auth/rider/signup?loginSuccess";
    }

    // 공백/널 입력값 방어용 체크
    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String normalizeStorePhone(String mobile) {
        if (mobile == null) {
            return "0000000000";
        }
        String digits = mobile.replaceAll("\\D", "");
        if (digits.length() == 11) {
            return digits.substring(1);
        }
        if (digits.length() >= 10) {
            return digits.substring(digits.length() - 10);
        }
        return String.format("%-10s", digits).replace(' ', '0');
    }

    // 라이더 이동수단 타입 파싱 (미입력 시 WALKING 기본값)
    private DelivererVehicleType parseVehicleType(String vehicleType) {
        if (vehicleType == null || vehicleType.isBlank()) {
            return DelivererVehicleType.WALKING;
        }
        try {
            return DelivererVehicleType.valueOf(vehicleType);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
    @PostMapping("/rider/logout")
    public String riderLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/rider/signup?logout";
    }
}
