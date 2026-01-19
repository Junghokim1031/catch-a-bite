package com.deliveryapp.catchabite.converter;

import com.deliveryapp.catchabite.dto.AddressDTO;
import com.deliveryapp.catchabite.entity.Address;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.entity.StoreOrder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
public class AddressConverter {

    /**
     * Converts Address Entity to AddressDTO.
     * Maps basic fields and extracts the AppUserId and Order IDs.
     */
    public AddressDTO toDto(Address entity) {
        if (entity == null) return null;

        return AddressDTO.builder()
                .addressId(entity.getAddressId())
                .appUserId(entity.getAppUser() != null ? entity.getAppUser().getAppUserId() : null)
                .addressDetail(entity.getAddressDetail())
                .addressNickname(entity.getAddressNickname())
                .addressEntranceMethod(entity.getAddressEntranceMethod())
                .addressIsDefault(entity.getAddressIsDefault())
                .addressCreatedDate(entity.getAddressCreatedDate() != null ? entity.getAddressCreatedDate().toString() : null)
                .addressVisible(entity.getAddressVisible())
                .storeOrderIds(entity.getOrders().stream()
                        .map(StoreOrder::getOrderId)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Converts AddressDTO to Address Entity.
     * The AppUser association is passed as a parameter, consistent with StoreOrderConverter.
     */
    public Address toEntity(AddressDTO dto, AppUser appUser) {
        if (dto == null) return null;

        return Address.builder()
                .addressId(dto.getAddressId())
                .appUser(appUser)
                .addressDetail(dto.getAddressDetail())
                .addressNickname(dto.getAddressNickname())
                .addressEntranceMethod(dto.getAddressEntranceMethod())
                .addressIsDefault(dto.getAddressIsDefault())
                .addressCreatedDate(dto.getAddressCreatedDate() != null ? LocalDate.parse(dto.getAddressCreatedDate()) : null)
                .addressVisible(dto.getAddressVisible())
                .build();
    }
}