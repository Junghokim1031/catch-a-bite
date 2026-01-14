package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "app_user")
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
    @Column(name = "app_user_id")
    private Long appUserId;

    @Column(name = "app_user_nickname", nullable = false, unique = true, length = 50)
    private String appUserNickname;

    @Column(name = "app_user_password", nullable = false, length = 255)
    private String appUserPassword;

    @Column(name = "app_user_name", nullable = false, length = 100)
    private String appUserName;

    @Column(name = "app_user_birthday")
    private LocalDate appUserBirthday;

    @Column(name = "app_user_gender", length = 1)
    private String appUserGender;

    @Column(name = "app_user_mobile", nullable = false, unique = true, length = 11)
    private String appUserMobile;

    @Column(name = "app_user_email", nullable = false, unique = true, length = 100)
    private String appUserEmail;

    @Column(name = "app_user_created_date")
    private LocalDateTime appUserCreatedDate;

    @Column(name = "app_user_status", length = 1)
    private String appUserStatus;

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

    public void changePassword(String encodedPassword) {
        this.appUserPassword = encodedPassword;
    }
}