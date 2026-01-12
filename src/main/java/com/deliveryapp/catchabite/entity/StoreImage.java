package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "STORE_IMAGE")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "store")
public class StoreImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STORE_IMAGE_ID")
    private Long storeImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID", nullable = false)
    private Store store;

    @Column(name = "STORE_IMAGE_URL", nullable = false, length = 500)
    private String storeImageUrl;
}
