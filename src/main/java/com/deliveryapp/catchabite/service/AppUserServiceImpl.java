package com.deliveryapp.catchabite.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliveryapp.catchabite.converter.AppUserConverter;
import com.deliveryapp.catchabite.dto.AppUserDTO;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.repository.AppUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    
    private final AppUserRepository appUserRepository;
    private AppUserConverter appUserConverter;


    //==========================================================
    // 기초 CRUD
    //==========================================================
    
    //Create
    @SuppressWarnings("null")
    @Override
    public AppUserDTO createUser(AppUserDTO dto) {
        try{
            if(dto == null){
                log.info("=============================================");
                log.info(dto.nullFieldsReport());
                log.info("=============================================");
                throw new IllegalArgumentException("프론트엔드에서 받은 DTO가 null입니다.");
            }
            log.info("=============================================");
            log.info(dto.nullFieldsReport());
            log.info("=============================================");
            AppUser entity = appUserConverter.toEntity(dto);
            AppUser saved = appUserRepository.save(entity);
            return appUserConverter.toDto(saved);
        }
        catch(Exception e){
            log.error("=============================================");
            log.error("프론트엔드에서 받은 DTO가 맞지 않습니다");
            log.error("=============================================");
            throw new IllegalArgumentException("프론트엔드에서 받은 DTO가 맞지 않습니다");
        }
    }

    //Read
    @Override
    public AppUserDTO readUser(Long appUserId) {
        // appUserId가 유효한지 확인
        validateAppUserId(appUserId, "readUser");
        
        try{
            //위에 Method에서 null확인함.
            @SuppressWarnings("null")
            AppUser entity = appUserRepository.findById(appUserId)
                .orElse(null);
            return appUserConverter.toDto(entity);
        }
        catch(Exception e){
            log.error("=============================================");
            log.error("Entity가 존재하지 않습니다.");
            log.error("=============================================");
            throw new IllegalArgumentException("Entity가 존재하지 않습니다.");
        }
    }

    //Update
    @Override
    @Transactional
    public AppUserDTO updateUser(Long appUserId, AppUserDTO dto) {

        // appUserId가 유효한지 확인
        validateAppUserId(appUserId, "updateUser");

        //DTO 확인
        log.info("업데이트 전 DTO 상태: {}", dto.nullFieldsReport());
        log.error("=============================================");
        log.error("프론트엔드에서 받은 DTO에 문제가 있습니다.");
        log.error("=============================================");


        // Id가 일치하는지 확인
        if(appUserId.equals(dto.getAppUserId())){
            log.error("=============================================");
            log.error("경로 ID({})와 DTO ID({})가 일치하지 않습니다.", appUserId, dto.getAppUserId());
            log.error("=============================================");
            throw new IllegalArgumentException("프론트엔드에서 받은 ID와 DTO의 ID가 일치하지 않습니다.");
        }

        // 존재하는 사용자인지 확인
        AppUser appUser = appUserRepository.findById(appUserId)
            .orElse(null);
        
        //존재하지 않는 사용자라면 오류 던짐
        if(appUser == null){
            log.error("=============================================");
            log.error("존재하지 않는 사용자입니다. ID: " + appUserId);
            log.error("=============================================");
            throw new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + appUserId);
        }

        // 사용자 정보 수정
        appUser.updateInfo(
            dto.getAppUserNickname(),
            dto.getAppUserEmail(),
            dto.getAppUserMobile()
        );

        return appUserConverter.toDto(appUser);
    }

    //Delete    
    //Method에서 null확인함.
    @SuppressWarnings("null")
    @Override
    public void deleteUser(Long appUserId) {
        // appUserId가 유효한지 확인
        validateAppUserId(appUserId, "deleteUser");
        
        appUserRepository.deleteById(appUserId);
    }

    //==========================================================
    // Helper Methods
    //==========================================================
    private void validateAppUserId(Long appUserId, String methodName) {
        if (appUserId == null || appUserId <= 0) {
            log.error("=============================================");
            log.error("{}에서 받은 appUserId가 유효하지 않습니다. ID: {}", methodName, appUserId);
            log.error("=============================================");
            throw new IllegalArgumentException(methodName + "에서 받은 appUserId가 null이거나 유효하지 않습니다.");
        }
    }
}