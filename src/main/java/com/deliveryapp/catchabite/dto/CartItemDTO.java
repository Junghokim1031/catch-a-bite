package com.deliveryapp.catchabite.dto;

import java.util.List;

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

    private String menuName;            //이름
    private Long menuPrice;             //가격
    private String menuImageUrl;        //이미지
    private Long totalItemPrice; // menuPrice * quantity

    // 요청용: 선택된 메뉴 옵션 ID 리스트
    private List<Long> optionIds;
    // 응답용: 화면에 보여줄 옵션 목록
    private List<String> menuOptions;
    /**
      * 디버깅용: DTO 매핑 후 null인 필드들을 로그로 출력함.
      * 생성/조회 시 의도치 않은 필드 누락을 감지하기 위해 사용.
      */
    public String nullFieldsReport() {
        return DTOChecker.nullFieldsReport(this);
    }
}
