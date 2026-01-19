package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.AddressDTO;

public interface AddressService {

    public AddressDTO createAddress(AddressDTO dto);
    public AddressDTO readAddress(Long addressId);
    public AddressDTO updateAddress(Long addressId, AddressDTO dto);
    public void deleteAddress(Long addressId);

}
