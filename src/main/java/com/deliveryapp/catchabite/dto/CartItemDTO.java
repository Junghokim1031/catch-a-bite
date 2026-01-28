package com.deliveryapp.catchabite.dto;

import com.deliveryapp.catchabite.common.util.DTOChecker;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CartItemDTO {

    private Long cartItemId;            //PK
    private Long cartId;                //FK
    private Long menuId;                //FK
    private Integer cartItemQuantity;

    private String menuName;
    private Long menuPrice;
    private String menuImageUrl;
    private Long totalItemPrice; // menuPrice * quantity
    /**
      * 디버깅용: DTO 매핑 후 null인 필드들을 로그로 출력함.
      * 생성/조회 시 의도치 않은 필드 누락을 감지하기 위해 사용.
      */
    public String nullFieldsReport() {
        return DTOChecker.nullFieldsReport(this);
    }
}
