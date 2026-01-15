package com.deliveryapp.catchabite.domain.enumtype;

public enum StoreOpenStatus {

    OPEN("open"),
    CLOSE("close");

    private final String value;

    StoreOpenStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static StoreOpenStatus from(String value) {
        return StoreOpenStatus.valueOf(value.trim().toUpperCase());
    }
}
