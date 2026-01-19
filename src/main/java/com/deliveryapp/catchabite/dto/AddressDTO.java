package com.deliveryapp.catchabite.dto;

import com.deliveryapp.catchabite.common.util.DTOChecker;
import lombok.*;
import java.util.List;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
@ToString(exclude = "storeOrderIds")
public class AddressDTO {
    private Long addressId;
    private Long appUserId;
    private String addressDetail;
    private String addressNickname;
    private String addressEntranceMethod;
    private String addressIsDefault;
    private String addressCreatedDate;
    private String addressVisible;
    private List<Long> storeOrderIds;       // Mapping the list of orders

    public String nullFieldsReport() {
        return DTOChecker.nullFieldsReport(this);
    }
}