package com.deliveryapp.catchabite.auth.security;

import com.deliveryapp.catchabite.common.constant.RoleConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class RoleBasedLoginSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException {

		for (GrantedAuthority authority : authentication.getAuthorities()) {
			String roleName = authority.getAuthority();

			if (RoleConstant.ROLE_USER.equals(roleName)) {
				response.sendRedirect("/user/main");
				return;
			}
			if (RoleConstant.ROLE_OWNER.equals(roleName)) {
				response.sendRedirect("/owner/main");
				return;
			}
			if (RoleConstant.ROLE_RIDER.equals(roleName)) {
				response.sendRedirect("/rider/main");
				return;
			}
		}

		response.sendRedirect("/");
	}
}
