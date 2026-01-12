package com.deliveryapp.catchabite.auth.controller;

import com.deliveryapp.catchabite.auth.service.MeService;
import com.deliveryapp.catchabite.entity.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class MeController {

    private final MeService meService;

    @GetMapping("/{appUserId}")
    public AppUser me(@PathVariable Long appUserId) {
        return meService.getMe(appUserId);
    }
}
