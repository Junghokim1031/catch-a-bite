package com.deliveryapp.catchabite.dto;

import com.deliveryapp.catchabite.common.util.DTOChecker;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@ToString
public class FavoriteStoreDTO {
    private Long favoriteId;   // PK
    private Long appUserId;    // FK (사용자)
    private Long storeId;      // FK (가게)

    public String nullFieldsReport() {
        return DTOChecker.nullFieldsReport(this);
    }
}