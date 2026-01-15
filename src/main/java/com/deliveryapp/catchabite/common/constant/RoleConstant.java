package com.deliveryapp.catchabite.common.constant;

/**
 * 사용자 권한(Role) 상수 정의
 * Spring Security 권한 체크용
 */
public final class RoleConstant {

    public static final String ROLE_USER = "ROLE_USER";             // 일반 사용자
    public static final String ROLE_STORE_OWNER = "ROLE_STORE_OWNER"; // 사장님
    public static final String ROLE_OWNER = "ROLE_OWNER";           // 사장님(legacy)
    public static final String ROLE_RIDER = "ROLE_RIDER";           // 배달원

    // 상수 클래스 인스턴스 생성 방지
    private RoleConstant() {
    }
}
