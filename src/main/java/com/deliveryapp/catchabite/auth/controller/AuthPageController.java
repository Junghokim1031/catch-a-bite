package com.deliveryapp.catchabite.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 인증(로그인/회원가입) 화면(Thymeleaf 템플릿) 라우팅 전용 Controller.
 * - API Controller(/api/v1/auth/**)와 분리해서, 브라우저 GET 요청은 여기서만 처리한다.
 */
@Controller
@RequestMapping("/auth")
public class AuthPageController {

    /**
     * 로그인 페이지
     * URL: GET /auth/login
     * View: templates/auth/login.html
     */
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    /**
     * 사용자(일반회원) 회원가입/로그인 페이지(한 화면에 토글 구성)
     * URL: GET /auth/user/signup
     * View: templates/auth/user_signup.html
     */
    @GetMapping("/user/signup")
    public String userSignupPage() {
        return "auth/user_signup";
    }

    /**
     * 점주 회원가입/로그인 페이지
     * URL: GET /auth/owner/signup
     * View: templates/auth/owner_signup.html
     */
    @GetMapping("/owner/signup")
    public String ownerSignupPage() {
        return "auth/owner_signup";
    }

    /**
     * 라이더 회원가입/로그인 페이지
     * URL: GET /auth/rider/signup
     * View: templates/auth/rider_signup.html
     */
    @GetMapping("/rider/signup")
    public String riderSignupPage() {
        return "auth/rider_signup";
    }

    /**
     * 역할 선택 페이지
     * URL: GET /auth/select
     * View: templates/auth/select.html
     */
    @GetMapping("/select")
    public String selectPage() {
        return "auth/select";
    }
}
