package com.deliveryapp.catchabite.auth.controller;

import com.deliveryapp.catchabite.auth.dto.OwnerSignupDTO;
import com.deliveryapp.catchabite.auth.dto.RiderSignupDTO;
import com.deliveryapp.catchabite.auth.dto.UserSignupDTO;
import com.deliveryapp.catchabite.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthPageController {

	private final AuthService authService;

	public AuthPageController(AuthService authService) {
		this.authService = authService;
	}

	@GetMapping("/")
	public String getIndex() {
		return "index";
	}

	@GetMapping("/auth/select")
	public String getSelect() {
		return "auth/select";
	}

	@GetMapping("/auth/login")
	public String getLogin() {
		return "auth/login";
	}

	@GetMapping("/auth/user/signup")
	public String getUserSignup(UserSignupDTO dto) {
		return "auth/user_signup";
	}

	@PostMapping("/auth/user/signup")
	public String postUserSignup(@Valid UserSignupDTO dto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "auth/user_signup";
		}
		authService.createUserAccount(dto);
		return "redirect:/auth/user/signup?signup=success";
	}

	@GetMapping("/auth/owner/signup")
	public String getOwnerSignup(OwnerSignupDTO dto) {
		return "auth/owner_signup";
	}

	@PostMapping("/auth/owner/signup")
	public String postOwnerSignup(@Valid OwnerSignupDTO dto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "auth/owner_signup";
		}
		authService.createOwnerAccount(dto);
		return "redirect:/auth/owner/signup?signup=success";
	}

	@GetMapping("/auth/rider/signup")
	public String getRiderSignup(RiderSignupDTO dto) {
		return "auth/rider_signup";
	}

	@PostMapping("/auth/rider/signup")
	public String postRiderSignup(@Valid RiderSignupDTO dto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "auth/rider_signup";
		}
		authService.createRiderAccount(dto);
		return "redirect:/auth/rider/signup?signup=success";
	}
}

