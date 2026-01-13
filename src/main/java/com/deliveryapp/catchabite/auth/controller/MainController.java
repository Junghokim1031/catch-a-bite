package com.deliveryapp.catchabite.auth.controller;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 유형별 메인 API 테스트용 컨트롤러
 * 권한/역할 분기 확인 목적
 */
@RestController
public class MainController {

    // 일반 사용자 메인 응답 (/user/main)
    @GetMapping("/user/main")
    public ApiResponse<String> getUserMain() {
        return ApiResponse.ok("OK - USER MAIN");
    }

    // 사장님 메인 응답 (/owner/main)
    @GetMapping("/owner/main")
    public ApiResponse<String> getOwnerMain() {
        return ApiResponse.ok("OK - OWNER MAIN");
    }

    // 배달원 메인 응답 (/rider/main)
    @GetMapping("/rider/main")
    public ApiResponse<String> getRiderMain() {
        return ApiResponse.ok("OK - RIDER MAIN");
    }
}
