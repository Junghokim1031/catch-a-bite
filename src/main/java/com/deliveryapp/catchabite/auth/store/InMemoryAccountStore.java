package com.deliveryapp.catchabite.auth.store;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAccountStore {

	public static class AccountRecord {
		private final String loginId;
		private final String encodedPassword;
		private final String roleName; // ROLE_USER / ROLE_OWNER / ROLE_RIDER

		public AccountRecord(String loginId, String encodedPassword, String roleName) {
			this.loginId = loginId;
			this.encodedPassword = encodedPassword;
			this.roleName = roleName;
		}

		public String getLoginId() {
			return loginId;
		}

		public String getEncodedPassword() {
			return encodedPassword;
		}

		public String getRoleName() {
			return roleName;
		}
	}

	private final Map<String, AccountRecord> accountMap = new ConcurrentHashMap<>();

	public boolean existsAccount(String loginId) {
		return accountMap.containsKey(loginId);
	}

	public void createAccount(AccountRecord accountRecord) {
		accountMap.put(accountRecord.getLoginId(), accountRecord);
	}

	public AccountRecord getAccount(String loginId) {
		return accountMap.get(loginId);
	}
}
