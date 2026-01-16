package com.deliveryapp.catchabite.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

// DeliveryController에서 이용
@Getter
@AllArgsConstructor
public class DeliveryApiResponseDTO<T> {

    // 업무 처리 성공 여부(배달 진행 상태가 정상적으로 동작하는 지 여부)
    private boolean success;

    private String message;
    private T data;

    // 정상적으로 진행될 때, 데이터가 있을 때
    public static <T> DeliveryApiResponseDTO<T> success(String message, T data) {
        return new DeliveryApiResponseDTO<T>(true, message, data);
    }

    // 정상적으로 진행될 때, 데이터가 없을 때
    public static DeliveryApiResponseDTO<Void> success(String message) {
        return new DeliveryApiResponseDTO<Void>(true, message, null);
    }

    // 비정상 처리되었을 때
    public static DeliveryApiResponseDTO<Void> fail(String message) {
        return new DeliveryApiResponseDTO<Void>(false, message, null);
    }

}
