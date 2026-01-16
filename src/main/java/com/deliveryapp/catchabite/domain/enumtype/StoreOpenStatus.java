package com.deliveryapp.catchabite.domain.enumtype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StoreOpenStatus {

    OPEN("open"),
    CLOSE("close");

    private final String value;

    StoreOpenStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static StoreOpenStatus from(String value) {
        return StoreOpenStatus.valueOf(value.trim().toUpperCase());
    }
}
