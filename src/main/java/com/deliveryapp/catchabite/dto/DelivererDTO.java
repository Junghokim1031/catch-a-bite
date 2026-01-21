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

    // (라이더 로그인 ID(email), 휴대폰 번호, PW)
    private String delivererEmail;            
    private String delivererMobile;
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
