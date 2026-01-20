package com.deliveryapp.catchabite.auth.api;

import com.deliveryapp.catchabite.auth.api.dto.ExistsResponse;
import com.deliveryapp.catchabite.auth.api.dto.LoginRequest;
import com.deliveryapp.catchabite.auth.api.dto.LoginResponse;
import com.deliveryapp.catchabite.auth.api.dto.MeResponse;
import com.deliveryapp.catchabite.auth.api.dto.SignUpRequest;
import com.deliveryapp.catchabite.auth.api.dto.SignUpResponse;
import com.deliveryapp.catchabite.auth.application.AuthService;
import com.deliveryapp.catchabite.common.util.RoleNormalizer;
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
        String accountType = request.accountType().trim().toUpperCase();
        String loginKey = request.loginKey().trim();

        // ✅ SecurityContext에 인증 저장 + 세션 저장(=JSESSIONID 내려가게 함)
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            // principal은 "ACCOUNT_TYPE:LOGIN_KEY" 형태로 저장
            accountType + ":" + loginKey,
            null,
            List.of(new SimpleGrantedAuthority(RoleNormalizer.normalize(response.roleName())))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        httpRequest.getSession(true);
        new HttpSessionSecurityContextRepository()
            .saveContext(SecurityContextHolder.getContext(), httpRequest, httpResponse);

        return response;
    }

    // 로그인ID 중복 체크 (프로젝트에 맞춰 유지)
    @GetMapping("/exists/login-id")
    public ExistsResponse existsLoginId(@RequestParam("loginId") String loginId) {
        return new ExistsResponse(authService.existsLoginId(loginId));
    }

    // 휴대폰 중복 체크
    @GetMapping("/exists/mobile")
    public ExistsResponse existsMobile(@RequestParam("mobile") String mobile) {
        return new ExistsResponse(authService.existsMobile(mobile));
    }

    // 닉네임 중복 체크
    @GetMapping("/exists/nickname")
    public ExistsResponse existsNickname(@RequestParam("nickname") String nickname) {
        return new ExistsResponse(authService.existsNickname(nickname));
    }

    // 로그인 사용자 정보 조회
    @GetMapping("/me")
    public MeResponse me() {
        return authService.getMe();
    }
}
