package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "APP_USER")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {
        "addresses",
        "favoriteStores",
        "carts",
        "orders",
        "reviews",
        "notifications"
})
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APP_USER_ID")
    private Long appUserId;

    @Column(
            name = "APP_USER_NICKNAME",
            unique = true,
            length = 50
    )
    private String appUserNickname;

    @Column(
            name = "APP_USER_PASSWORD",
            nullable = false,
            length = 255
    )
    private String appUserPassword;

    @Column(
            name = "APP_USER_NAME",
            nullable = false,
            length = 100
    )
    private String appUserName;

    @Column(name = "APP_USER_BIRTHDAY")
    private LocalDate appUserBirthday;

    @Column(
            name = "APP_USER_GENDER",
            length = 1
    )
    private String appUserGender;

    @Column(
            name = "APP_USER_MOBILE",
            nullable = false,
            unique = true,
            length = 11
    )
    private String appUserMobile;

    @Column(
            name = "APP_USER_EMAIL",
            nullable = false,
            unique = true,
            length = 100
    )
    private String appUserEmail;

    @Column(name = "APP_USER_CREATED_DATE")
    private LocalDate appUserCreatedDate;

    @Column(
            name = "APP_USER_STATUS",
            length = 1
    )
    private String appUserStatus;

    /* ===============================
       연관관계 (1:N)
       =============================== */

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    @Builder.Default
    private List<FavoriteStore> favoriteStores = new ArrayList<>();

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Cart> carts = new ArrayList<>();

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    @Builder.Default
    private List<StoreOrder> orders = new ArrayList<>();

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();
}
