package com.deliveryapp.catchabite.auth.api;

import com.deliveryapp.catchabite.auth.api.dto.ExistsResponse;
import com.deliveryapp.catchabite.auth.api.dto.StoreOwnerLoginRequest;
import com.deliveryapp.catchabite.auth.api.dto.StoreOwnerLoginResponse;
import com.deliveryapp.catchabite.auth.api.dto.StoreOwnerSignUpRequest;
import com.deliveryapp.catchabite.auth.service.StoreOwnerAuthService;
import com.deliveryapp.catchabite.common.constant.RoleConstant;
import com.deliveryapp.catchabite.common.exception.InvalidCredentialsException;
import com.deliveryapp.catchabite.common.util.RoleNormalizer;
import com.deliveryapp.catchabite.domain.enumtype.StoreCategory; // ✅ 추가
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.entity.StoreOwner;
import com.deliveryapp.catchabite.repository.StoreRepository;
import com.deliveryapp.catchabite.repository.StoreOwnerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

/**
 * StoreOwnerAuthController: 사장님 회원가입/로그인 API
 */
@RestController
@RequestMapping("/api/v1/store-owner/auth")
public class StoreOwnerAuthController {

	private final StoreOwnerRepository storeOwnerRepository;
	private final PasswordEncoder passwordEncoder;
	private final StoreRepository storeRepository;
	private final StoreOwnerAuthService storeOwnerAuthService;

	public StoreOwnerAuthController(
			StoreOwnerRepository storeOwnerRepository,
			PasswordEncoder passwordEncoder,
			StoreRepository storeRepository,
			StoreOwnerAuthService storeOwnerAuthService
	) {
		this.storeOwnerRepository = storeOwnerRepository;
		this.passwordEncoder = passwordEncoder;
		this.storeRepository = storeRepository;
		this.storeOwnerAuthService = storeOwnerAuthService;
	}

	@PostMapping("/signup")
	public String signup(@Valid @RequestBody StoreOwnerSignUpRequest request) {

		if (!request.password().equals(request.confirmPassword())) {
			throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
		}

		if (storeOwnerRepository.existsByStoreOwnerEmail(request.email())) {
			throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
		}

		if (storeOwnerRepository.existsByStoreOwnerMobile(request.mobile())) {
			throw new IllegalArgumentException("이미 사용 중인 휴대폰 번호입니다.");
		}

		if (storeOwnerRepository.existsByStoreOwnerBusinessRegistrationNo(request.businessRegistrationNumber())) {
			throw new IllegalArgumentException("이미 등록된 사업자 번호입니다.");
		}

		StoreOwner owner = StoreOwner.builder()
				.storeOwnerEmail(request.email())
				.storeOwnerPassword(passwordEncoder.encode(request.password()))
				.storeOwnerName(request.name())
				.storeOwnerMobile(request.mobile())
				.storeOwnerBusinessRegistrationNo(request.businessRegistrationNumber())
				.createdAt(LocalDateTime.now())
				.build();

		storeOwnerRepository.save(owner);

		Store store = Store.builder()
				.storeOwner(owner)
				.storeOwnerName(owner.getStoreOwnerName())
				.storeName(request.storeName())
				.storeAddress(request.storeAddress())
				.storeCategory(StoreCategory.etc) // ✅ 여기만 변경 (기존 "UNASSIGNED" 제거)
				.storePhone(normalizeStorePhone(request.mobile()))
				.build();

		storeRepository.save(store);

		return "ok";
	}

	@PostMapping("/login")
	public StoreOwnerLoginResponse login(@Valid @RequestBody StoreOwnerLoginRequest request,
										 HttpServletRequest httpRequest,
										 HttpServletResponse httpResponse) {

		StoreOwner owner = storeOwnerRepository.findByStoreOwnerEmail(request.email())
				.orElseThrow(() -> new InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다."));

		if (!owner.isActive()) {
			throw new IllegalArgumentException("비활성화된 계정입니다.");
		}

		if (!passwordEncoder.matches(request.password(), owner.getStoreOwnerPassword())) {
			throw new InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
		}

		StoreOwnerLoginResponse response = new StoreOwnerLoginResponse(
				owner.getStoreOwnerId(),
				owner.getStoreOwnerName(),
				RoleConstant.ROLE_STORE_OWNER
		);

		Authentication authentication = new UsernamePasswordAuthenticationToken(
				"OWNER:" + request.email(),
				null,
				List.of(new SimpleGrantedAuthority(RoleNormalizer.normalize(RoleConstant.ROLE_STORE_OWNER)))
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);
		httpRequest.getSession(true);
		new HttpSessionSecurityContextRepository()
				.saveContext(SecurityContextHolder.getContext(), httpRequest, httpResponse);

		return response;
	}

	@GetMapping("/exists/email")
	public ExistsResponse existsEmail(@RequestParam("email") String email) {
		return new ExistsResponse(storeOwnerAuthService.existsEmail(email));
	}

	@GetMapping("/exists/mobile")
	public ExistsResponse existsMobile(@RequestParam("mobile") String mobile) {
		return new ExistsResponse(storeOwnerAuthService.existsMobile(mobile));
	}

	@GetMapping("/exists/business-registration-number")
	public ExistsResponse existsBrn(@RequestParam("businessRegistrationNumber") String brn) {
		return new ExistsResponse(storeOwnerRepository.existsByStoreOwnerBusinessRegistrationNo(brn));
	}

	private String normalizeStorePhone(String mobile) {
		if (mobile == null) {
			return "0000000000";
		}
		String digits = mobile.replaceAll("\\D", "");
		if (digits.length() == 11) {
			return digits.substring(1);
		}
		if (digits.length() >= 10) {
			return digits.substring(digits.length() - 10);
		}
		return String.format("%-10s", digits).replace(' ', '0');
	}
}
