package com.deliveryapp.catchabite.auth.controller;

import com.deliveryapp.catchabite.auth.dto.OwnerSignupDTO;
import com.deliveryapp.catchabite.auth.dto.RiderSignupDTO;
import com.deliveryapp.catchabite.auth.dto.UserSignupDTO;
import com.deliveryapp.catchabite.auth.service.AuthService;
import com.deliveryapp.catchabite.common.response.ApiResponse;
import jakarta.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

    @GetMapping("/select")
    public ApiResponse<String> getSelect() {
		System.out.println("AuthController - getSelect");
		return ApiResponse.ok(
			"OK - auth select - signup: POST /api/auth/user/signup | /api/auth/owner/signup | /api/auth/rider/signup - login: GET /login"
		);
	}

	@PostMapping("/user/signup")
	public ApiResponse<Void> createUser(@Valid UserSignupDTO userSignupDTO) {
		authService.createUserAccount(userSignupDTO);
		return ApiResponse.okMessage("user signup ok");
	}

	@PostMapping("/owner/signup")
	public ApiResponse<Void> createOwner(@Valid OwnerSignupDTO ownerSignupDTO) {
		authService.createOwnerAccount(ownerSignupDTO);
		return ApiResponse.okMessage("owner signup ok");
	}

	@PostMapping("/rider/signup")
	public ApiResponse<Void> createRider(@Valid RiderSignupDTO riderSignupDTO) {
		authService.createRiderAccount(riderSignupDTO);
		return ApiResponse.okMessage("rider signup ok");
	}

	@PostMapping("/auth/user/signup")
	public String postUserSignup(@Valid UserSignupDTO dto, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) return "auth/user_signup";

    authService.createUserAccount(dto);
    return "redirect:/auth/login?signup=success";
	}

}