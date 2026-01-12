package com.deliveryapp.catchabite.auth.security;

import com.deliveryapp.catchabite.auth.domain.Account;
import com.deliveryapp.catchabite.auth.port.AccountStorePort;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final AccountStorePort accountStorePort;

	public CustomUserDetailsService(AccountStorePort accountStorePort) {
		this.accountStorePort = accountStorePort;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Account account = accountStorePort.findByLoginId(username)
			.orElseThrow(() ->
				new UsernameNotFoundException("User not found: " + username)
			);

		return User.builder()
			.username(account.getLoginId())
			.password(account.getEncodedPassword())
			.roles(account.getRoleName().replace("ROLE_", ""))
			.build();
	}
}
