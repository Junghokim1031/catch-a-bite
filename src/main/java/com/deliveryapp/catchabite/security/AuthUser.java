package com.deliveryapp.catchabite.security;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.ToString;

/**
 * [User/Store/Deliverer]DeliveryController에서 security를 적용하기 위해 추가함 - 01/20
 *
 * Session 기반(Spring Security + JSESSIONID) 사용 시 principal이 세션에 저장되므로
 *    Serializable 구현을 권장.
 */
@Getter
@ToString(exclude = "password")
public class AuthUser implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long userId;        // 고객 PK
    private final Long delivererId;   // 배달원 PK (없으면 null)
    private final Long storeOwnerId;  // 점주 PK (없으면 null)
    private final Long deliveryId;    // order_delivery PK (없으면 null)

    private final String email;
    private final String password;    // 세션 기반에서는 굳이 담지 않아도 됨(null 가능)
    private final String role;        // USER, RIDER, STORE_OWNER

    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Full constructor (기존 AllArgsConstructor 대체)
     */
    public AuthUser(
            Long userId,
            Long delivererId,
            Long storeOwnerId,
            Long deliveryId,
            String email,
            String password,
            String role,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.userId = userId;
        this.delivererId = delivererId;
        this.storeOwnerId = storeOwnerId;
        this.deliveryId = deliveryId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.authorities = (authorities == null) ? Collections.emptyList() : authorities;
    }

    /**
     * password 없이 principal 만들기 (세션 기반 로그인 컨트롤러에서 사용 추천)
     */
    public AuthUser(
            Long userId,
            Long delivererId,
            Long storeOwnerId,
            Long deliveryId,
            String email,
            String role,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this(userId, delivererId, storeOwnerId, deliveryId, email, null, role, authorities);
    }

    /**
     * Rider 전용 팩토리 (deliverer 로그인 시 가장 많이 사용)
     */
    public static AuthUser rider(
            Long delivererId,
            String email,
            Collection<? extends GrantedAuthority> authorities
    ) {
        return new AuthUser(
                null,              // userId
                delivererId,
                null,              // storeOwnerId
                null,              // deliveryId
                email,
                null,              // password
                "RIDER",
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password; // null 가능
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
