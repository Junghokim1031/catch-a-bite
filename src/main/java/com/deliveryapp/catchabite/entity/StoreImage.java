package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "store_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class StoreImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_image_id", nullable = false)
    private Long storeImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "store_image_url")
    private String storeImageUrl;
}
