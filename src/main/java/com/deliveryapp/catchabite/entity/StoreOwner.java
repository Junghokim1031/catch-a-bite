package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "store_owner",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_store_owner_email", columnNames = "store_owner_email"),
                @UniqueConstraint(name = "uk_store_owner_mobile", columnNames = "store_owner_mobile"),
                @UniqueConstraint(
                        name = "uk_store_owner_business_registration_no",
                        columnNames = "store_owner_business_registration_no"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class StoreOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_owner_id", nullable = false)
    private Long storeOwnerId;

    @Column(name = "store_owner_email", nullable = false, length = 255)
    private String storeOwnerEmail;

    @Column(name = "store_owner_password", nullable = false, length = 255)
    private String storeOwnerPassword;

    @Column(name = "store_owner_name", nullable = false, length = 100)
    private String storeOwnerName;

    @Column(name = "store_owner_mobile", nullable = false, length = 11)
    private String storeOwnerMobile;

    @Column(name = "store_owner_business_registration_no", length = 20, unique = true)
    private String storeOwnerBusinessRegistrationNo;

    /**
     * 'Y' or 'N'
     */
    @Column(name = "store_owner_status", length = 1)
    private String storeOwnerStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (this.storeOwnerStatus == null) this.storeOwnerStatus = "Y";
    }

    public boolean isActive() {
        return "Y".equalsIgnoreCase(storeOwnerStatus);
    }
}
