package com.deliveryapp.catchabite.auth.controller;

import com.deliveryapp.catchabite.auth.service.AuthService;
import com.deliveryapp.catchabite.entity.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public AppUser signup(@RequestBody AppUser appUser) {
        return authService.signup(appUser);
    }

    @PostMapping("/login")
    public AppUser login(
        @RequestParam String loginId,
        @RequestParam String password
    ) {
        return authService.login(loginId, password);
    }
}
