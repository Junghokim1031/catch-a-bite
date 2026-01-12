package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "FAVORITE_STORE")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteStore {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "FAVORITE_ID")
	private Long favoriteId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "APP_USER_ID", nullable = false)
	private AppUser appUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STORE_ID", nullable = false)
	private Store store;
}
