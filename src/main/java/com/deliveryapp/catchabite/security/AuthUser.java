package com.deliveryapp.catchabite.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Getter;

// [User/Store/Deliverer]DeliveryController에서 security를 적용하기 위해 추가함 - 01/20
@Getter
@AllArgsConstructor
public class AuthUser implements UserDetails {

    private Long userId;        // 고객 PK
    private Long delivererId;   // 배달원 PK (없으면 null)
    private Long storeOwnerId;  // 점주 PK (없으면 null)
    private Long deliveryId;    // order_delivery(주문 배달) PK - 배달 정보를 조회하기 위해 이용

    private String email;
    private String password;
    private String role;        // USER, RIDER, STORE_OWNER

    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}