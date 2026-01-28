package com.deliveryapp.catchabite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuImageDTO {

    private Long menuImageId;
    private Long menuId;
    private Long storeId;

    /**
     * DB에는 URL만 저장
     * - 파일 업로드는 별도 API에서 처리(업로드 후 생성된 URL 저장)
     */
    private String menuImageUrl;

    /**
     * 대표 이미지 여부
     */
    private Boolean menuImageIsMain;
}
