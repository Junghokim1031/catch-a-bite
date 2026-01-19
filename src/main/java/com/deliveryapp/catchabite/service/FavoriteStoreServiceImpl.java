package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.converter.FavoriteStoreConverter;
import com.deliveryapp.catchabite.dto.FavoriteStoreDTO;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.entity.FavoriteStore;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.repository.AppUserRepository;
import com.deliveryapp.catchabite.repository.FavoriteStoreRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class FavoriteStoreServiceImpl implements FavoriteStoreService {

    private final FavoriteStoreRepository favoriteStoreRepository;
    private final AppUserRepository appUserRepository;
    private final StoreRepository storeRepository;
    private final FavoriteStoreConverter favoriteStoreConverter;

    //validateDto에서 null확인함
    @SuppressWarnings("null")
    @Override
    @Transactional
    public FavoriteStoreDTO addFavorite(FavoriteStoreDTO dto) {
        validateDto(dto, "addFavorite");
        
        try {
            // 중복 체크
            if (favoriteStoreRepository.existsByAppUser_AppUserIdAndStore_StoreId(dto.getAppUserId(), dto.getStoreId())) {
                logAndThrowError("addFavorite", "이미 즐겨찾기에 등록된 가게입니다.", "StoreID: " + dto.getStoreId());
            }

            AppUser appUser = appUserRepository.findById(dto.getAppUserId()).orElse(null);
            Store store = storeRepository.findById(dto.getStoreId()).orElse(null);

            FavoriteStore entity = favoriteStoreConverter.toEntity(dto, appUser, store);
            FavoriteStore saved = favoriteStoreRepository.save(entity);
            return favoriteStoreConverter.toDto(saved);
        } catch (Exception e) {
            logAndThrowError("addFavorite", "즐겨찾기 등록 중 오류가 발생했습니다.", dto.nullFieldsReport());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteStoreDTO> getFavoritesByUser(Long appUserId) {
        if (appUserId == null){
            logAndThrowError("getFavoritesByUser", "사용자 ID가 없습니다.", null);
        } 
        
        return favoriteStoreRepository.findByAppUser_AppUserId(appUserId).stream()
            .map(favoriteStoreConverter::toDto)
            .collect(Collectors.toList());
    }

    //validateFavoriteId에서 null확인함
    @SuppressWarnings("null")
    @Override
    @Transactional
    public void removeFavorite(Long favoriteId) {
        validateFavoriteId(favoriteId, "removeFavorite");
        
        if (!favoriteStoreRepository.existsById(favoriteId)) {
            logAndThrowError("removeFavorite", "존재하지 않는 즐겨찾기 ID입니다.", "ID: " + favoriteId);
        }
        favoriteStoreRepository.deleteById(favoriteId);
    }

    //==========================================================
    // Helper Methods (AddressServiceImpl 스타일)
    //==========================================================

    private void validateFavoriteId(Long id, String methodName) {
        if (id == null || id <= 0) {
            logAndThrowError(methodName, "유효하지 않은 즐겨찾기 ID입니다.", "ID: " + id);
        }
    }

    private void validateDto(FavoriteStoreDTO dto, String methodName) {
        if (dto == null) {
            logAndThrowError(methodName, "DTO가 null입니다.", null);
        }
    }

    private void logAndThrowError(String methodName, String userMsg, String detailMsg) {
        log.error("=============================================");
        log.error("[{}] {}", methodName, userMsg);
        if (detailMsg != null) log.error("상세 정보: {}", detailMsg);
        log.error("=============================================");
        throw new IllegalArgumentException(userMsg);
    }
}