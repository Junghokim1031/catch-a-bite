package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ADDRESS")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ADDRESS_ID")
	private Long addressId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "APP_USER_ID", nullable = false)
	private AppUser appUser;

	@Column(name = "ADDRESS_DETAIL", nullable = false, length = 255)
	private String addressDetail;

	@Column(name = "ADDRESS_NICKNAME", length = 50)
	private String addressNickname;

	@Column(name = "ADDRESS_ENTRANCE_METHOD", length = 100)
	private String addressEntranceMethod;

	@Column(name = "ADDRESS_IS_DEFAULT", length = 1)
	private String addressIsDefault;

	@Column(name = "ADDRESS_CREATED_DATE")
	private LocalDate addressCreatedDate;

	@Column(name = "ADDRESS_VISIBLE", length = 1)
	private String addressVisible;

	@OneToMany(mappedBy = "address", cascade = CascadeType.ALL)
	@Builder.Default
	private List<StoreOrder> orders = new ArrayList<>();
}
