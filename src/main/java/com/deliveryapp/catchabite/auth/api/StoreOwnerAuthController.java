package com.deliveryapp.catchabite.auth.api;

import com.deliveryapp.catchabite.auth.api.dto.StoreOwnerLoginRequest;
import com.deliveryapp.catchabite.auth.api.dto.StoreOwnerLoginResponse;
import com.deliveryapp.catchabite.auth.api.dto.StoreOwnerSignUpRequest;
import com.deliveryapp.catchabite.common.constant.RoleConstant;
import com.deliveryapp.catchabite.common.exception.InvalidCredentialsException;
import com.deliveryapp.catchabite.entity.StoreOwner;
import com.deliveryapp.catchabite.repository.StoreOwnerRepository;
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

/**
 * StoreOwnerAuthController: 사장님 회원가입/로그인 API
 */
@RestController
@RequestMapping("/api/v1/store-owner/auth")
public class StoreOwnerAuthController {

    private final StoreOwnerRepository storeOwnerRepository;
    private final PasswordEncoder passwordEncoder;

    public StoreOwnerAuthController(StoreOwnerRepository storeOwnerRepository, PasswordEncoder passwordEncoder) {
        this.storeOwnerRepository = storeOwnerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 사장님 회원가입 (약관/심사 없이 즉시 활성)
    @PostMapping("/signup")
    public String signup(@Valid @RequestBody StoreOwnerSignUpRequest request) {

        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        if (storeOwnerRepository.existsByStoreOwnerEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (storeOwnerRepository.existsByStoreOwnerMobile(request.mobile())) {
            throw new IllegalArgumentException("이미 사용 중인 휴대폰 번호입니다.");
        }

        StoreOwner owner = StoreOwner.builder()
            .storeOwnerEmail(request.email())
            .storeOwnerPassword(passwordEncoder.encode(request.password()))
            .storeOwnerName(request.name())
            .storeOwnerMobile(request.mobile())
            // status는 엔티티 @PrePersist에서 null이면 Y로 자동 세팅됨
            .createdAt(LocalDateTime.now())
            .build();

        storeOwnerRepository.save(owner);
        return "ok";
    }

    // 사장님 로그인
    @PostMapping("/login")
    public StoreOwnerLoginResponse login(@Valid @RequestBody StoreOwnerLoginRequest request,
                                         HttpServletRequest httpRequest,
                                         HttpServletResponse httpResponse) {

        StoreOwner owner = storeOwnerRepository.findByStoreOwnerEmail(request.email())
            .orElseThrow(() -> new InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!owner.isActive()) {
            throw new IllegalArgumentException("비활성화된 계정입니다.");
        }

        if (!passwordEncoder.matches(request.password(), owner.getStoreOwnerPassword())) {
            throw new InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        StoreOwnerLoginResponse response = new StoreOwnerLoginResponse(
            owner.getStoreOwnerId(),
            owner.getStoreOwnerName(),
            RoleConstant.ROLE_STORE_OWNER
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            request.email(),
            null,
            List.of(new SimpleGrantedAuthority(RoleConstant.ROLE_STORE_OWNER))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        httpRequest.getSession(true);
        new HttpSessionSecurityContextRepository()
            .saveContext(SecurityContextHolder.getContext(), httpRequest, httpResponse);

        return response;
    }

    // 이메일 중복 체크
    @GetMapping("/exists/email")
    public boolean existsEmail(@RequestParam String email) {
        return storeOwnerRepository.existsByStoreOwnerEmail(email);
    }

    // 휴대폰 중복 체크
    @GetMapping("/exists/mobile")
    public boolean existsMobile(@RequestParam String mobile) {
        return storeOwnerRepository.existsByStoreOwnerMobile(mobile);
    }
}
