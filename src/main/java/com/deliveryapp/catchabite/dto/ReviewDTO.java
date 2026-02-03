package com.deliveryapp.catchabite.dto;

import com.deliveryapp.catchabite.common.util.DTOChecker;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ReviewDTO {

    private Long reviewId;                  //PK
    private Long storeOrderId;              //FK
    private Long appUserId;                 //FK
    private Long storeId;                   //FK
    private String authorNickname;
    private BigDecimal reviewRating;
    private String reviewContent;
    private LocalDateTime reviewCreatedAt;
    private String ownerReplyContent;
    private LocalDateTime ownerReplyCreatedAt;
    /**
      * 디버깅용: DTO 매핑 후 null인 필드들을 로그로 출력함.
      * 생성/조회 시 의도치 않은 필드 누락을 감지하기 위해 사용.
      */
    public String nullFieldsReport() {
        return DTOChecker.nullFieldsReport(this);
    }
}
