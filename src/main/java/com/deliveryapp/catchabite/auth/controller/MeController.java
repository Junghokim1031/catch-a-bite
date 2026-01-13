package com.deliveryapp.catchabite.auth.controller;

import com.deliveryapp.catchabite.auth.service.MeService;
import com.deliveryapp.catchabite.entity.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 내 정보 조회 API 컨트롤러
 * AppUser 단건 조회 담당
 */
@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class MeController {

    // 내 정보 조회 로직 담당
    private final MeService meService;

    // 회원 ID 기준 내 정보 조회 (/me/{appUserId})
    @GetMapping("/{appUserId}")
    public AppUser me(@PathVariable Long appUserId) {
        return meService.getMe(appUserId);
    }
}
