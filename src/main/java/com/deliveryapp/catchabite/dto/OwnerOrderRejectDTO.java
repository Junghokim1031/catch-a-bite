package com.deliveryapp.catchabite.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerOrderRejectDTO {
	private String reason;
}