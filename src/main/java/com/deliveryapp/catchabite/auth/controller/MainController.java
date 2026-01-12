package com.deliveryapp.catchabite.auth.controller;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

	@GetMapping("/user/main")
	public ApiResponse<String> getUserMain() {
		return ApiResponse.ok("OK - USER MAIN");
	}

	@GetMapping("/owner/main")
	public ApiResponse<String> getOwnerMain() {
		return ApiResponse.ok("OK - OWNER MAIN");
	}

	@GetMapping("/rider/main")
	public ApiResponse<String> getRiderMain() {
		return ApiResponse.ok("OK - RIDER MAIN");
	}
}
