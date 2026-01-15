package com.deliveryapp.catchabite.auth.api;

import com.deliveryapp.catchabite.auth.api.dto.LoginRequest;
import com.deliveryapp.catchabite.auth.api.dto.LoginResponse;
import com.deliveryapp.catchabite.auth.api.dto.SignUpRequest;
import com.deliveryapp.catchabite.auth.api.dto.SignUpResponse;
import com.deliveryapp.catchabite.auth.application.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

/**
 * 인증/회원가입 관련 API 컨트롤러
 * - 로그인 성공 시 세션(JSESSIONID) 생성 및 SecurityContext 저장
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 개인 회원가입
    @PostMapping("/signup")
    public SignUpResponse signup(@Valid @RequestBody SignUpRequest request) {
        return authService.signUp(request);
    }

    // 개인 로그인 (세션 기반)
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request,
                               HttpServletRequest httpRequest,
                               HttpServletResponse httpResponse) {

        // 기존 로그인 검증/응답 생성
        LoginResponse response = authService.login(request);

        // ✅ SecurityContext에 인증 저장 + 세션 저장(=JSESSIONID 내려가게 함)
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            // principal은 "loginId/email"이 정석 (DTO 필드명은 프로젝트에 맞게 유지)
            request.loginKey(),
            null,
            List.of(new SimpleGrantedAuthority(response.roleName()))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        httpRequest.getSession(true);
        new HttpSessionSecurityContextRepository()
            .saveContext(SecurityContextHolder.getContext(), httpRequest, httpResponse);

        return response;
    }

    // 로그인ID 중복 체크 (프로젝트에 맞춰 유지)
    @GetMapping("/exists/login-id")
    public boolean existsLoginId(@RequestParam String loginId) {
        return authService.existsLoginId(loginId);
    }

    // 휴대폰 중복 체크
    @GetMapping("/exists/mobile")
    public boolean existsMobile(@RequestParam String mobile) {
        return authService.existsMobile(mobile);
    }

    // 닉네임 중복 체크
    @GetMapping("/exists/nickname")
    public boolean existsNickname(@RequestParam String nickname) {
        return authService.existsNickname(nickname);
    }
}
