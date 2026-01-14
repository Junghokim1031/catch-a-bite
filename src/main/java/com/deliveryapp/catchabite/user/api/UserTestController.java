package com.deliveryapp.catchabite.user.api;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserTestController {

    @GetMapping("/user/me")
    public ApiResponse<Map<String, Object>> me(HttpServletRequest request, Authentication authentication) {
        return ApiResponse.ok(buildPayload(request, authentication));
    }

    @GetMapping("/user/profile")
    public ApiResponse<Map<String, Object>> profile(HttpServletRequest request, Authentication authentication) {
        return ApiResponse.ok(buildPayload(request, authentication));
    }

    @GetMapping("/rider/ping")
    public ApiResponse<Map<String, Object>> riderPing(HttpServletRequest request, Authentication authentication) {
        return ApiResponse.ok(buildPayload(request, authentication));
    }

    @GetMapping("/owner/ping")
    public ApiResponse<Map<String, Object>> ownerPing(HttpServletRequest request, Authentication authentication) {
        return ApiResponse.ok(buildPayload(request, authentication));
    }

    private Map<String, Object> buildPayload(HttpServletRequest request, Authentication authentication) {
        String principal = authentication != null ? authentication.getName() : "anonymous";
        return Map.of(
            "ok", true,
            "path", request.getRequestURI(),
            "principal", principal
        );
    }
}
