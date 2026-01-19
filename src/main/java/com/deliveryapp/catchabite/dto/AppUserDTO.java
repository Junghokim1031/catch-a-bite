package com.deliveryapp.catchabite.dto;

import java.util.List;

import com.deliveryapp.catchabite.common.util.DTOChecker;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder (toBuilder = true)
@ToString (exclude = {"Address", "FavoriteStore", "CartId", "StoreOrder", "Review", "Notification"})
public class AppUserDTO {
    
    private Long appUserId;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하로 입력해주세요.")
    private String appUserNickname;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String appUserPassword;

    @NotBlank(message = "이름은 필수입니다.")
    private String appUserName;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "생년월일은 YYYY-MM-DD 형식이어야 합니다.")
    private String appUserBirthday;

    @Pattern(regexp = "^[MF]$", message = "성별은 'M' 또는 'F'여야 합니다.")
    private String appUserGender;

    @NotBlank(message = "휴대폰 번호는 필수입니다.")
    @Pattern(regexp = "^010\\d{8}$", message = "휴대폰 번호는 010으로 시작하는 11자리 숫자여야 합니다.")
    private String appUserMobile;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String appUserEmail;

    private String appUserCreatedDate;

    @Pattern(regexp = "^[YN]$", message = "상태값은 'Y' 또는 'N'이어야 합니다.")
    private String appUserStatus;
    
    private List<Long> Address;         // 주소 테이블 객체 Id의 목록
    private List<Long> FavoriteStore;   // 즐겨찾기 가게 테이블 객체 Id의 목록
    private Long CartId;                // 장바구니 테이블 객체 Id의 목록
    private List<Long> StoreOrder;      // 주문 테이블 객체 Id의 목록
    private List<Long> Review;          // 리뷰 테이블 객체 Id의 목록
    private List<Long> Notification;    // 알림 테이블 객체 Id의 목록

    /**
      * 디버깅용: DTO 매핑 후 null인 필드들을 로그로 출력함.
      * 생성/조회 시 의도치 않은 필드 누락을 감지하기 위해 사용.
      */
    public String nullFieldsReport() {
        return DTOChecker.nullFieldsReport(this);
    }
}
