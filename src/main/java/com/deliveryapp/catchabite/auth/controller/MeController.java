package com.deliveryapp.catchabite.auth.controller;

import com.deliveryapp.catchabite.auth.dto.AccountDTO;
import com.deliveryapp.catchabite.auth.dto.ProfileUpdateDTO;
import com.deliveryapp.catchabite.auth.service.MeService;
import com.deliveryapp.catchabite.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
public class MeController {

	private final MeService meService;

	public MeController(MeService meService) {
		this.meService = meService;
	}

	@GetMapping
	public ApiResponse<AccountDTO> getMe(Authentication authentication) {
		System.out.println("MeController - getMe");
		String loginId = authentication.getName();
		return ApiResponse.ok(meService.getMe(loginId));
	}

	@PatchMapping
	public ApiResponse<AccountDTO> updateMe(Authentication authentication, @Valid @RequestBody ProfileUpdateDTO profileUpdateDTO) {
		System.out.println("MeController - updateMe");
		String loginId = authentication.getName();
		return ApiResponse.ok(meService.updateMe(loginId, profileUpdateDTO));
	}
}
