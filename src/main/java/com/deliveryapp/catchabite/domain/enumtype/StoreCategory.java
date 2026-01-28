package com.deliveryapp.catchabite.domain.enumtype;

import java.util.Arrays;

public enum StoreCategory {
	korean,
	japanese,
	chinese,
	western,
	snack,
	chicken,
	pizza,
	cafe_dessert,
	late_night,
	etc;

	public static StoreCategory from(String value) {
		if (value == null) {
			throw new IllegalArgumentException("storeCategory is null");
		}
		return Arrays.stream(values())
				.filter(v -> v.name().equalsIgnoreCase(value.trim()))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("invalid storeCategory: " + value));
	}
}
