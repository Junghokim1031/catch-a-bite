package com.deliveryapp.catchabite.auth.application;

import com.deliveryapp.catchabite.auth.api.dto.*;
import com.deliveryapp.catchabite.common.constant.RoleConstant;
import com.deliveryapp.catchabite.common.exception.InvalidCredentialsException;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.entity.Deliverer;
import com.deliveryapp.catchabite.entity.StoreOwner;
import com.deliveryapp.catchabite.repository.AppUserRepository;
import com.deliveryapp.catchabite.repository.DelivererRepository;
import com.deliveryapp.catchabite.repository.StoreOwnerRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 인증/회원가입 비즈니스 로직 구현체
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final StoreOwnerRepository storeOwnerRepository;
    private final DelivererRepository delivererRepository;

    // 사용자 저장소 및 비밀번호 암호화 도구 주입
    public AuthServiceImpl(
        AppUserRepository appUserRepository,
        PasswordEncoder passwordEncoder,
        StoreOwnerRepository storeOwnerRepository,
        DelivererRepository delivererRepository
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.storeOwnerRepository = storeOwnerRepository;
        this.delivererRepository = delivererRepository;
    }

    // 회원가입 처리 및 사용자 계정 생성
    @Override
    public SignUpResponse signUp(SignUpRequest request) {

        // 필수 약관 미동의 시 가입 불가
        if (!request.requiredTermsAccepted()) {
            throw new IllegalArgumentException("Required terms must be accepted.");
        }

        // 비밀번호 확인 불일치 검증
        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Password confirmation does not match.");
        }

        // 이메일/휴대폰/닉네임 중복 검증
        if (appUserRepository.existsByAppUserEmail(request.loginId())) {
            throw new IllegalArgumentException("Email is already in use.");
        }
        if (appUserRepository.existsByAppUserMobile(request.mobile())) {
            throw new IllegalArgumentException("Mobile number is already in use.");
        }
        if (appUserRepository.existsByAppUserNickname(request.nickname())) {
            throw new IllegalArgumentException("Nickname is already in use.");
        }

        // 사용자 계정 저장
        AppUser saved = appUserRepository.save(
            AppUser.builder()
                .appUserEmail(request.loginId())
                .appUserPassword(passwordEncoder.encode(request.password()))
                .appUserNickname(request.nickname())
                .appUserMobile(request.mobile())
                .appUserName(request.name())
                .appUserCreatedDate(LocalDateTime.now())
                .build()
        );

        // 회원가입 결과 반환
        return new SignUpResponse(
            saved.getAppUserId(),
            saved.getAppUserEmail(),
            saved.getAppUserNickname(),
            RoleConstant.ROLE_USER
        );
    }

    // 로그인 처리 및 사용자 인증
    @Override
    public LoginResponse login(LoginRequest request) {
        String accountType = normalizeAccountType(request.accountType());
        String loginKey = request.loginKey().trim();

        if ("USER".equals(accountType)) {
            AppUser account = appUserRepository
                .findByAppUserEmailOrAppUserMobile(loginKey, loginKey)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials."));

            if (!passwordEncoder.matches(request.password(), account.getAppUserPassword())) {
                throw new InvalidCredentialsException("Invalid credentials.");
            }

            return new LoginResponse(
                account.getAppUserId(),
                account.getAppUserNickname(),
                RoleConstant.ROLE_USER
            );
        }

        if ("OWNER".equals(accountType)) {
            StoreOwner owner = storeOwnerRepository
                .findByStoreOwnerEmail(loginKey)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials."));

            if (!owner.isActive()) {
                throw new InvalidCredentialsException("Invalid credentials.");
            }

            if (!passwordEncoder.matches(request.password(), owner.getStoreOwnerPassword())) {
                throw new InvalidCredentialsException("Invalid credentials.");
            }

            return new LoginResponse(
                owner.getStoreOwnerId(),
                owner.getStoreOwnerName(),
                RoleConstant.ROLE_STORE_OWNER
            );
        }

        if ("RIDER".equals(accountType)) {
            Deliverer deliverer = delivererRepository
                .findByDelivererEmail(loginKey)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials."));

            if (!passwordEncoder.matches(request.password(), deliverer.getDelivererPassword())) {
                throw new InvalidCredentialsException("Invalid credentials.");
            }

            return new LoginResponse(
                deliverer.getDelivererId(),
                deliverer.getDelivererEmail(),
                RoleConstant.ROLE_RIDER
            );
        }

        throw new IllegalArgumentException("Invalid account type.");
    }

    // 이메일 중복 여부 확인
    @Override
    public boolean existsLoginId(String loginId) {
        return appUserRepository.existsByAppUserEmail(loginId);
    }

    // 휴대폰 번호 중복 여부 확인
    @Override
    public boolean existsMobile(String mobile) {
        return appUserRepository.existsByAppUserMobile(mobile);
    }

    // 닉네임 중복 여부 확인
    @Override
    public boolean existsNickname(String nickname) {
        return appUserRepository.existsByAppUserNickname(nickname);
    }

    // 로그인 사용자 정보 조회
    @Override
    public MeResponse getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
            || !authentication.isAuthenticated()
            || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("Unauthorized");
        }
        String principal = authentication.getName();
        String[] parts = parsePrincipal(principal);
        String accountType = parts[0];
        String loginKey = parts[1];
        String roleName = resolveRole(authentication);

        if ("USER".equals(accountType)) {
            AppUser appUser = appUserRepository
                .findByAppUserEmailOrAppUserMobile(loginKey, loginKey)
                .orElseThrow(() -> new IllegalStateException("Unauthorized"));
            return new MeResponse(
                appUser.getAppUserId(),
                loginKey,
                appUser.getAppUserName(),
                roleName != null ? roleName : RoleConstant.ROLE_USER,
                appUser.getAppUserMobile(),
                accountType
            );
        }

        if ("OWNER".equals(accountType)) {
            StoreOwner storeOwner = storeOwnerRepository
                .findByStoreOwnerEmail(loginKey)
                .orElseThrow(() -> new IllegalStateException("Unauthorized"));
            return new MeResponse(
                storeOwner.getStoreOwnerId(),
                loginKey,
                storeOwner.getStoreOwnerName(),
                roleName != null ? roleName : RoleConstant.ROLE_STORE_OWNER,
                storeOwner.getStoreOwnerMobile(),
                accountType
            );
        }

        if ("RIDER".equals(accountType)) {
            Deliverer deliverer = delivererRepository
                .findByDelivererEmail(loginKey)
                .orElseThrow(() -> new IllegalStateException("Unauthorized"));
            return new MeResponse(
                deliverer.getDelivererId(),
                loginKey,
                loginKey,
                roleName != null ? roleName : RoleConstant.ROLE_RIDER,
                null,
                accountType
            );
        }

        throw new IllegalStateException("Unauthorized");
    }

    private String resolveRole(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        return authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .orElse(null);
    }

    private String normalizeAccountType(String accountType) {
        if (accountType == null) {
            return "";
        }
        return accountType.trim().toUpperCase();
    }

    private String[] parsePrincipal(String principal) {
        if (principal == null || !principal.contains(":")) {
            throw new IllegalStateException("Unauthorized");
        }
        String[] parts = principal.split(":", 2);
        if (parts.length != 2) {
            throw new IllegalStateException("Unauthorized");
        }
        String accountType = parts[0].trim().toUpperCase();
        String loginKey = parts[1].trim();
        if (accountType.isBlank() || loginKey.isBlank()) {
            throw new IllegalStateException("Unauthorized");
        }
        return new String[] { accountType, loginKey };
    }
}
