package com.deliveryapp.catchabite.auth.api;

import com.deliveryapp.catchabite.auth.api.dto.DelivererLoginRequest;
import com.deliveryapp.catchabite.auth.api.dto.DelivererSignUpRequest;
import com.deliveryapp.catchabite.auth.api.dto.ExistsResponse;
import com.deliveryapp.catchabite.auth.service.DelivererAuthService;
import com.deliveryapp.catchabite.common.constant.RoleConstant;
import com.deliveryapp.catchabite.common.util.RoleNormalizer;
import com.deliveryapp.catchabite.common.exception.InvalidCredentialsException;
import com.deliveryapp.catchabite.domain.enumtype.DelivererVehicleType;
import com.deliveryapp.catchabite.domain.enumtype.YesNo;
import com.deliveryapp.catchabite.entity.Deliverer;
import com.deliveryapp.catchabite.repository.DelivererRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/** 라이더 인증 API 컨트롤러 */
@RestController
@RequestMapping("/api/v1/deliverer/auth")
public class DelivererAuthController {

    private final DelivererRepository delivererRepository;
    private final PasswordEncoder passwordEncoder;
    private final DelivererAuthService delivererAuthService;

    public DelivererAuthController(
        DelivererRepository delivererRepository,
        PasswordEncoder passwordEncoder,
        DelivererAuthService delivererAuthService
    ) {
        this.delivererRepository = delivererRepository;
        this.passwordEncoder = passwordEncoder;
        this.delivererAuthService = delivererAuthService;
    }

    // 라이더 회원가입 API
    @PostMapping("/signup")
    public String signup(@Valid @RequestBody DelivererSignUpRequest request) {

        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

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
            .delivererMobile(request.mobile())
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
    public String login(@Valid @RequestBody DelivererLoginRequest request,
                        HttpServletRequest httpRequest,
                        HttpServletResponse httpResponse) {

        Deliverer deliverer = delivererRepository.findByDelivererEmail(request.email())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials."));

        if (!passwordEncoder.matches(request.password(), deliverer.getDelivererPassword())) {
            throw new InvalidCredentialsException("Invalid credentials.");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            "RIDER:" + request.email(),
            null,
            List.of(new SimpleGrantedAuthority(RoleNormalizer.normalize(RoleConstant.ROLE_RIDER)))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        httpRequest.getSession(true);
        new HttpSessionSecurityContextRepository()
            .saveContext(SecurityContextHolder.getContext(), httpRequest, httpResponse);

        return "ok";
    }

    // 이메일 중복 체크
    @GetMapping("/exists/email")
    public ExistsResponse existsEmail(@RequestParam("email") String email) {
        return new ExistsResponse(delivererAuthService.existsEmail(email));
    }

    // 휴대폰 중복 체크
    @GetMapping("/exists/mobile")
    public ExistsResponse existsMobile(@RequestParam("mobile") String mobile) {
        return new ExistsResponse(delivererAuthService.existsMobile(mobile));
    }

    private boolean isBlank(String v) {
        return v == null || v.isBlank();
    }
}
