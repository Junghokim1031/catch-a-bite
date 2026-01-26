package com.deliveryapp.catchabite.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerPaymentDTO {
    private Long paymentId;
    private Long orderId;

    private String paymentMethod;
    private Long paymentAmount;
    private String paymentStatus;
    private LocalDateTime paymentPaidAt;
}
