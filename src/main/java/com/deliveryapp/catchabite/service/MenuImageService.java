package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.MenuImageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MenuImageService {

    List<MenuImageDTO> getMenuImages(Long storeOwnerId, Long storeId, Long menuId);

    /**
     * 이미지 파일 업로드 + DB 저장
     * - 첫 이미지(or setMain=true)는 대표 이미지로 설정
     */
    List<MenuImageDTO> uploadMenuImages(Long storeOwnerId, Long storeId, Long menuId,
                                        List<MultipartFile> images,
                                        Boolean setFirstAsMain);

    MenuImageDTO createMenuImageByUrl(Long storeOwnerId, Long storeId, Long menuId, MenuImageDTO dto);

    void setMainImage(Long storeOwnerId, Long storeId, Long menuId, Long menuImageId);

    void deleteMenuImage(Long storeOwnerId, Long storeId, Long menuId, Long menuImageId);
}
