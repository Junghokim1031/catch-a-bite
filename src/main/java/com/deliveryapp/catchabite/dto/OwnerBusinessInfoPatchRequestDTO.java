package com.deliveryapp.catchabite.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * '사업자 정보' 수정용 PATCH 요청 DTO
 * - null인 필드는 변경하지 않습니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OwnerBusinessInfoPatchRequestDTO {

    @Size(max = 100)
    private String ownerName;

    @Size(max = 100)
    private String businessName;

    @Size(max = 400)
    private String businessAddress;

    @Size(max = 50)
    private String businessRegistrationNo;
}
