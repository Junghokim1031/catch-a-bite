package com.deliveryapp.catchabite.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 내 정보 수정 요청 DTO
 * 닉네임/휴대폰 번호 변경용
 */
public class ProfileUpdateDTO {

    // 사용자 닉네임 (필수)
    @NotBlank(message = "nickname is required")
    private String nickname;

    // 휴대폰 번호 (숫자 11자리)
    @NotBlank(message = "mobile is required")
    @Pattern(regexp = "^[0-9]{11}$")
    private String mobile;

    // nickname 반환
    public String getNickname() {
        return nickname;
    }

    // nickname 설정
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    // mobile 반환
    public String getMobile() {
        return mobile;
    }

    // mobile 설정
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
