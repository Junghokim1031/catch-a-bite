package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.dto.AddressDTO;
import com.deliveryapp.catchabite.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/appuser/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<ApiResponse<AddressDTO>> createAddress(@RequestBody AddressDTO dto) {
        AddressDTO createdAddress = addressService.createAddress(dto);
        return ResponseEntity.ok(ApiResponse.ok(createdAddress));
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressDTO>> readAddress(@PathVariable Long addressId) {
        AddressDTO address = addressService.readAddress(addressId);
        return ResponseEntity.ok(ApiResponse.ok(address));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressDTO>> updateAddress(@PathVariable Long addressId, @RequestBody AddressDTO dto) {
        AddressDTO updatedAddress = addressService.updateAddress(addressId, dto);
        return ResponseEntity.ok(ApiResponse.ok(updatedAddress));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok(ApiResponse.okMessage("Address deleted successfully"));
    }
}