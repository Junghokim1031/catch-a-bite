package com.deliveryapp.catchabite.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Owner 가게관리 화면의 '사업자 정보' 섹션용 DTO
 * - store_owner + store 정보를 합쳐서 제공합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OwnerBusinessInfoDTO {

    /** 대표자명 */
    @Size(max = 100)
    private String ownerName;

    /** 상호명(가게명) */
    @Size(max = 100)
    private String businessName;

    /** 사업자주소(가게 주소) */
    @Size(max = 400)
    private String businessAddress;

    /** 사업자등록번호 */
    @Size(max = 50)
    private String businessRegistrationNo;
}
