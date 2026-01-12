package com.deliveryapp.catchabite.auth.store;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InMemoryStoreConfig {

	@Bean
	public InMemoryAccountStore inMemoryAccountStore() {
		return new InMemoryAccountStore();
	}
}
