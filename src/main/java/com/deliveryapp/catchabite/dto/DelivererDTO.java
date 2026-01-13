package com.deliveryapp.catchabite.dto;

import java.time.LocalDateTime;

import com.deliveryapp.catchabite.domain.enumtype.DelivererVehicleType;
import com.deliveryapp.catchabite.domain.enumtype.YesNo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DelivererDTO {

    private Long delivererId;              

    // 1월 12일 새로 추가한 부분(라이더 로그인 ID, PW)
    private String delivererEmail;            

    private String delivererPassword;

    @NotNull
    private DelivererVehicleType delivererVehicleType;        

    @Size(max = 50)
    private String delivererLicenseNumber;     
    
    @Size(max = 50)
    private String delivererVehicleNumber;      

    private YesNo delivererStatus;                  
    private LocalDateTime delivererLastLoginDate;       
    private YesNo delivererVerified;                         

}
