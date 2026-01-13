package com.deliveryapp.catchabite.auth.api;

import com.deliveryapp.catchabite.auth.api.dto.*;
import com.deliveryapp.catchabite.auth.application.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 인증/회원가입 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    // 인증 관련 비즈니스 로직 서비스 주입
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 서버 상태 확인용 핑 API
    @GetMapping("/ping")
    public String ping() {
        return "ok";
    }

    // 회원가입 처리 API
    @PostMapping("/signup")
    public SignUpResponse signup(@Valid @RequestBody SignUpRequest request) {
        return authService.signUp(request);
    }

    // 로그인 처리 API
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // 로그인 ID(이메일) 중복 여부 확인 API
    @GetMapping("/exists/login-id")
    public ExistsResponse existsLoginId(@RequestParam String loginId) {
        return new ExistsResponse(authService.existsLoginId(loginId));
    }

    // 휴대폰 번호 중복 여부 확인 API
    @GetMapping("/exists/mobile")
    public ExistsResponse existsMobile(@RequestParam String mobile) {
        return new ExistsResponse(authService.existsMobile(mobile));
    }

    // 닉네임 중복 여부 확인 API
    @GetMapping("/exists/nickname")
    public ExistsResponse existsNickname(@RequestParam String nickname) {
        return new ExistsResponse(authService.existsNickname(nickname));
    }
}
