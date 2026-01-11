package com.deliveryapp.catchabite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderOptionDTO {

    private Long orderOptionId;
    private Long orderItemId;
    private String orderOptionName;
    private Long orderOptionExtraPrice;
    
}
