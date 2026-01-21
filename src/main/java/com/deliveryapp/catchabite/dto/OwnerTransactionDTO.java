package com.deliveryapp.catchabite.dto;

import com.deliveryapp.catchabite.domain.enumtype.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OwnerTransactionDTO {

    private Long transactionId;
    private TransactionType transactionType;

    private Long orderId; // relatedEntityId (ORDER)

    private Long amount;
    private String currency;
    private String transactionStatus;

    private String portonePaymentId;
    private String portoneTransferId;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
