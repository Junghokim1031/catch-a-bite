package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "NOTIFICATION")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "NOTIFICATION_ID")
	private Long notificationId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "APP_USER_ID", nullable = false)
	private AppUser appUser;

	@Column(name = "NOTIFICATION_MESSAGE", nullable = false, length = 500)
	private String notificationMessage;

	@Column(name = "NOTIFICATION_IS_READ", length = 1)
	private String notificationIsRead;

	@Column(name = "NOTIFICATION_CREATED_AT")
	private LocalDate notificationCreatedAt;
}
