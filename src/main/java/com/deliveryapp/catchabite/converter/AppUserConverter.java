package com.deliveryapp.catchabite.converter;

import com.deliveryapp.catchabite.dto.AppUserDTO;
import com.deliveryapp.catchabite.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
public class AppUserConverter {

    /**
     * Converts AppUser Entity to AppUserDTO
     * Maps basic fields, formats dates, and extracts IDs from associated lists.
     */
    public AppUserDTO toDto(AppUser entity) {
        if (entity == null) return null;

        return AppUserDTO.builder()
                .appUserId(entity.getAppUserId())
                .appUserNickname(entity.getAppUserNickname())
                .appUserPassword(entity.getAppUserPassword())
                .appUserName(entity.getAppUserName())
                .appUserBirthday(entity.getAppUserBirthday() != null ? entity.getAppUserBirthday().toString() : null)
                .appUserGender(entity.getAppUserGender())
                .appUserMobile(entity.getAppUserMobile())
                .appUserEmail(entity.getAppUserEmail())
                .appUserCreatedDate(entity.getAppUserCreatedDate() != null ? entity.getAppUserCreatedDate().toString() : null)
                .appUserStatus(entity.getAppUserStatus())
                // Mapping associated entity lists to ID lists as defined in your DTO
                .Address(entity.getAddresses().stream()
                        .map(Address::getAddressId)
                        .collect(Collectors.toList()))
                .FavoriteStore(entity.getFavoriteStores().stream()
                        .map(FavoriteStore::getFavoriteId)
                        .collect(Collectors.toList()))
                // Taking the first Cart ID if available, as DTO specifies a single Long CartId
                .CartId(entity.getCarts() != null && !entity.getCarts().isEmpty() 
                        ? entity.getCarts().get(0).getCartId() : null)
                .StoreOrder(entity.getOrders().stream()
                        .map(StoreOrder::getOrderId)
                        .collect(Collectors.toList()))
                .Review(entity.getReviews().stream()
                        .map(Review::getReviewId)
                        .collect(Collectors.toList()))
                .Notification(entity.getNotifications().stream()
                        .map(Notification::getNotificationId)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Converts AppUserDTO to AppUser Entity
     * Maps core user information and parses date strings. 
     * Associations (Addresses, Orders, etc.) are typically managed via specific services.
     */
    public AppUser toEntity(AppUserDTO dto) {
        if (dto == null) return null;

        return AppUser.builder()
                .appUserId(dto.getAppUserId())
                .appUserNickname(dto.getAppUserNickname())
                .appUserPassword(dto.getAppUserPassword())
                .appUserName(dto.getAppUserName())
                .appUserBirthday(dto.getAppUserBirthday() != null ? LocalDate.parse(dto.getAppUserBirthday()) : null)
                .appUserGender(dto.getAppUserGender())
                .appUserMobile(dto.getAppUserMobile())
                .appUserEmail(dto.getAppUserEmail())
                .appUserCreatedDate(dto.getAppUserCreatedDate() != null ? LocalDateTime.parse(dto.getAppUserCreatedDate()) : null)
                .appUserStatus(dto.getAppUserStatus())
                .build();
    }
}