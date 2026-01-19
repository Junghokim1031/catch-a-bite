package com.deliveryapp.catchabite.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliveryapp.catchabite.converter.AddressConverter;
import com.deliveryapp.catchabite.dto.AddressDTO;
import com.deliveryapp.catchabite.entity.Address;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.repository.AddressRepository;
import com.deliveryapp.catchabite.repository.AppUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AppUserRepository appUserRepository;
    private final AddressRepository addressRepository;
    private final AddressConverter addressConverter;
    
    //AppUser가 null인 경우, .orElse로 처리되며 
    @SuppressWarnings("null")
    @Override
    public AddressDTO createAddress(AddressDTO dto) {

        // 잘못된 DTO라면 오류 던짐
        validateDto(dto, "createAddress");

        try{
            log.info("=============================================");
            log.info("프론트엔드에서 받은 DTO 상태: {}", dto.nullFieldsReport());
            log.info("=============================================");

            AppUser appUser = appUserRepository.findById(dto.getAppUserId())
                .orElse(null);
            Address entity = addressConverter.toEntity(dto, appUser);
            Address saved = addressRepository.save(entity);
            return addressConverter.toDto(saved);
        }
        catch(Exception e){
            log.error("=============================================");
            log.error("프론트엔드에서 받은 DTO가 맞지 않습니다");
            log.error(dto.nullFieldsReport());
            log.error("=============================================");
            throw new IllegalArgumentException("프론트엔드에서 받은 DTO가 맞지 않습니다");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDTO readAddress(Long addressId) {
        // 잘못된 ID라면 오류 던짐
        validateAddressId(addressId,"readAddress");

        //위에 Method에서 null확인함.
        @SuppressWarnings("null")
        Address entity = addressRepository.findById(addressId)
            .orElse(null);

        if(entity== null){
            log.error("=============================================");
            log.error("존재하지 않는 주소입니다. ID: " + addressId);
            log.error("=============================================");
            throw new IllegalArgumentException("존재하지 않는 주소입니다. ID: " + addressId);
        }

        return addressConverter.toDto(entity);
    }


    @Override
    @Transactional
    public AddressDTO updateAddress(Long addressId, AddressDTO dto) {
        
        // 잘못된 ID라면 오류 던짐
        validateAddressId(addressId,"updateAddress");

        // 잘못된 DTO라면 오류 던짐
        validateDto(dto, "updateAddress");

        //위에 Method에서 null확인함.
        @SuppressWarnings("null")
        Address entity = addressRepository.findById(addressId)
            .orElse(null);

        // 존재하지 않는 주소라면 오류 던짐
        if(dto == null){
            log.error("=============================================");
            log.error("프론트엔드에서 받은 DTO가 null입니다.");
            log.error("=============================================");
            throw new IllegalArgumentException("존재하지 않는 주소입니다. ID:");
        }

        //DTO 내 정보 확인
        log.info("업데이트 전 DTO 상태: {}", dto.nullFieldsReport());

        // 수정할 ID와 DTO의 ID가 일치하는지 확인
        if(!addressId.equals(dto.getAddressId())){
            log.error("=============================================");
            log.error("경로 ID({})와 DTO ID({})가 일치하지 않습니다.", addressId, dto.getAddressId());
            log.error("=============================================");
            throw new IllegalArgumentException("경로 ID와 DTO ID가 일치하지 않습니다.");
        }

        entity.updateInfo(
            dto.getAddressDetail(), 
            dto.getAddressNickname(), 
            dto.getAddressEntranceMethod(), 
            dto.getAddressIsDefault());

        return addressConverter.toDto(entity);        
    }

    //Method에서 null확인함.
    @SuppressWarnings("null")
    @Override
    public void deleteAddress(Long addressId) {
        // 잘못된 ID라면 오류 던짐
        validateAddressId(addressId,"deleteAddress");
        
        
        addressRepository.deleteById(addressId);
    }

    //==========================================================
    // Helper Methods
    //==========================================================
    private void validateAddressId(Long addressId, String methodName) {
        if (addressId == null || addressId <= 0) {
            log.error("=============================================");
            log.error("{}에서 받은 addressId가 유효하지 않습니다. ID: {}", methodName, addressId);
            log.error("=============================================");
            throw new IllegalArgumentException(methodName + "에서 받은 addressId가 null이거나 유효하지 않습니다.");
        }
    }

    private void validateDto(Object dto, String methodName) {
        if (dto == null) {
            log.error("=============================================");
            log.error(methodName + "에서 받은 DTO가 null입니다.");
            log.error("=============================================");
            throw new IllegalArgumentException(methodName + "에서 받은 DTO가 null입니다.");
        }
    }
}
