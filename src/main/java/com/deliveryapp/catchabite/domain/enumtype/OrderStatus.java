package com.deliveryapp.catchabite.domain.enumtype;

public enum OrderStatus {

    PENDING("pending"),
    COOKING("cooking"),
    COOKED("cooked"),
    PAYMENTCONFIRMED("payment_confirmed"),
    DELIVERED("delivered"),
    REJECTED("rejected");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    /** API 응답 / 화면 표시용 */
    public String getValue() {
        return value;
    }

    /** 외부 입력(String)을 enum으로 변환 */
    public static OrderStatus from(String value) {
        return OrderStatus.valueOf(value.trim().toUpperCase());
    }
}
