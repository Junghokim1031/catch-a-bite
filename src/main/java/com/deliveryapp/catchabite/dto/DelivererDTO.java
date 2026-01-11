package com.deliveryapp.catchabite.dto;

import java.time.LocalDateTime;

import com.deliveryapp.catchabite.domain.enumtype.YesNo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DelivererDTO {

    private Long delivererId;                   // BIGINT -> Long
    private String delivererVehicleType;        // VARCHAR(50)
    private String delivererLicenseNumber;      // VARCHAR(50), NULL 가능
    private String delivererVehicleNumber;      // VARCHAR(50), NULL 가능

    private YesNo delivererStatus;                  // CHAR(1) -> Character(널 가능)
    private LocalDateTime delivererLastLoginDate;       // DATETIME, NULL 가능
    private YesNo delivererVerified;                // CHAR(1) -> Character(널 가능)            

}
