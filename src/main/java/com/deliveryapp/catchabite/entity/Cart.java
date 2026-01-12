package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CART")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CART_ID")
	private Long cartId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "APP_USER_ID", nullable = false)
	private AppUser appUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STORE_ID", nullable = false)
	private Store store;

	@Column(name = "CART_UPDATE_AT")
	private LocalDate cartUpdateAt;

	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
	@Builder.Default
	private List<CartItem> cartItems = new ArrayList<>();
}
