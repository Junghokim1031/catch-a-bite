package com.deliveryapp.catchabite.auth.security;

import com.deliveryapp.catchabite.auth.store.InMemoryAccountStore;
import com.deliveryapp.catchabite.auth.store.InMemoryAccountStore.AccountRecord;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class InMemoryUserDetailsService implements UserDetailsService {

	private final InMemoryAccountStore inMemoryAccountStore;

	public InMemoryUserDetailsService(InMemoryAccountStore inMemoryAccountStore) {
		this.inMemoryAccountStore = inMemoryAccountStore;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AccountRecord accountRecord = inMemoryAccountStore.getAccount(username);

		if (accountRecord == null) {
			throw new UsernameNotFoundException("account not found");
		}

		return User.builder()
			.username(accountRecord.getLoginId())
			.password(accountRecord.getEncodedPassword())
			.authorities(accountRecord.getRoleName())
			.build();
	}
}
