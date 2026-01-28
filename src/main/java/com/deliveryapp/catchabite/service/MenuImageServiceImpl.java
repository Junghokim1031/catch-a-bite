package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.MenuImageDTO;
import com.deliveryapp.catchabite.entity.Menu;
import com.deliveryapp.catchabite.entity.MenuImage;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.repository.MenuImageRepository;
import com.deliveryapp.catchabite.repository.MenuRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuImageServiceImpl implements MenuImageService {

    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final MenuImageRepository menuImageRepository;

    /**
     * application.properties 기준(현재 main.zip): com.deliveryapp.catchabite=C:\\upload
     */
    @Value("${com.deliveryapp.catchabite}")
    private String uploadRoot;

    @Override
    public List<MenuImageDTO> getMenuImages(Long storeOwnerId, Long storeId, Long menuId) {

        // 권한 체크: storeOwner -> store -> menu
        Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
                .orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

        Menu menu = menuRepository.findByMenuIdAndStore_StoreId(menuId, store.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("메뉴가 존재하지 않습니다. menuId=" + menuId));

        return menuImageRepository.findAllByMenu_MenuIdOrderByMenuImageIsMainDescMenuImageIdAsc(menu.getMenuId())
                .stream()
                .map(img -> MenuImageDTO.builder()
                        .menuImageId(img.getMenuImageId())
                        .menuId(menu.getMenuId())
                        .storeId(storeId)
                        .menuImageUrl(img.getMenuImageUrl())
                        .menuImageIsMain(img.getMenuImageIsMain())
                        .build())
                .toList();
    }

    @Override
    public List<MenuImageDTO> uploadMenuImages(Long storeOwnerId, Long storeId, Long menuId,
                                               List<MultipartFile> images,
                                               Boolean setFirstAsMain) {

        if (images == null || images.isEmpty()) {
            return List.of();
        }

        Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
                .orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

        Menu menu = menuRepository.findByMenuIdAndStore_StoreId(menuId, store.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("메뉴가 존재하지 않습니다. menuId=" + menuId));

        boolean wantSetMain = setFirstAsMain == null ? true : setFirstAsMain;

        List<MenuImageDTO> result = new ArrayList<>();

        // 업로드 경로: {uploadRoot}/uploads/menus/{menuId}/
        Path baseDir = Paths.get(uploadRoot, "uploads", "menus", String.valueOf(menuId));
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            throw new IllegalStateException("업로드 디렉토리를 생성할 수 없습니다: " + baseDir, e);
        }

        for (int i = 0; i < images.size(); i++) {
            MultipartFile file = images.get(i);
            if (file == null || file.isEmpty()) {
                continue;
            }

            // 간단한 이미지 타입 검증(실무에서는 더 엄격하게)
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다. contentType=" + contentType);
            }

            String originalName = file.getOriginalFilename() == null ? "image" : file.getOriginalFilename();
            String ext = extractExtension(originalName);
            String storedName = UUID.randomUUID() + ext;

            Path target = baseDir.resolve(storedName);
            try {
                file.transferTo(target);
            } catch (IOException e) {
                throw new IllegalStateException("파일 저장에 실패했습니다. file=" + originalName, e);
            }

            // 외부 접근 URL (ResourceHandler에서 /uploads/** 매핑)
            String publicUrl = "/uploads/menus/" + menuId + "/" + storedName;

            boolean isMain = wantSetMain && i == 0;

            // 첫 이미지가 메인인 경우: 기존 메인 해제 + menu.thumbnail 업데이트
            if (isMain) {
                clearMainImage(menu);
                menu.changeThumbnailUrl(publicUrl);
            }

            MenuImage saved = menuImageRepository.save(MenuImage.builder()
                    .menu(menu)
                    .menuImageUrl(publicUrl)
                    .menuImageIsMain(isMain)
                    .build());

            result.add(MenuImageDTO.builder()
                    .menuImageId(saved.getMenuImageId())
                    .menuId(menu.getMenuId())
                    .storeId(storeId)
                    .menuImageUrl(saved.getMenuImageUrl())
                    .menuImageIsMain(saved.getMenuImageIsMain())
                    .build());
        }

        return result;
    }

    @Override
    public MenuImageDTO createMenuImageByUrl(Long storeOwnerId, Long storeId, Long menuId, MenuImageDTO dto) {

        if (dto == null || dto.getMenuImageUrl() == null || dto.getMenuImageUrl().isBlank()) {
            throw new IllegalArgumentException("menuImageUrl is required");
        }

        Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
                .orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

        Menu menu = menuRepository.findByMenuIdAndStore_StoreId(menuId, store.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("메뉴가 존재하지 않습니다. menuId=" + menuId));

        boolean isMain = dto.getMenuImageIsMain() != null && dto.getMenuImageIsMain();
        if (isMain) {
            clearMainImage(menu);
            menu.changeThumbnailUrl(dto.getMenuImageUrl());
        }

        MenuImage saved = menuImageRepository.save(MenuImage.builder()
                .menu(menu)
                .menuImageUrl(dto.getMenuImageUrl())
                .menuImageIsMain(isMain)
                .build());

        // 대표 이미지가 아직 없고, 이번에 isMain=false로 저장했다면: 썸네일은 유지
        if (menu.getMenuThumbnailUrl() == null && isMain) {
            menu.changeThumbnailUrl(dto.getMenuImageUrl());
        }

        return MenuImageDTO.builder()
                .menuImageId(saved.getMenuImageId())
                .menuId(menu.getMenuId())
                .storeId(storeId)
                .menuImageUrl(saved.getMenuImageUrl())
                .menuImageIsMain(saved.getMenuImageIsMain())
                .build();
    }

    @Override
    public void setMainImage(Long storeOwnerId, Long storeId, Long menuId, Long menuImageId) {

        Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
                .orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

        Menu menu = menuRepository.findByMenuIdAndStore_StoreId(menuId, store.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("메뉴가 존재하지 않습니다. menuId=" + menuId));

        MenuImage target = menuImageRepository.findByMenuImageIdAndMenu_MenuId(menuImageId, menuId)
                .orElseThrow(() -> new IllegalArgumentException("이미지가 존재하지 않습니다. menuImageId=" + menuImageId));

        clearMainImage(menu);
        target.changeMain(true);
        menu.changeThumbnailUrl(target.getMenuImageUrl());
    }

    @Override
    public void deleteMenuImage(Long storeOwnerId, Long storeId, Long menuId, Long menuImageId) {

        Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
                .orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

        Menu menu = menuRepository.findByMenuIdAndStore_StoreId(menuId, store.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("메뉴가 존재하지 않습니다. menuId=" + menuId));

        MenuImage target = menuImageRepository.findByMenuImageIdAndMenu_MenuId(menuImageId, menuId)
                .orElseThrow(() -> new IllegalArgumentException("이미지가 존재하지 않습니다. menuImageId=" + menuImageId));

        boolean wasMain = Boolean.TRUE.equals(target.getMenuImageIsMain());

        menuImageRepository.delete(target);

        // 메인 삭제 시: 남아있는 이미지 중 첫 번째를 메인으로 승격(실무에서 흔한 처리)
        if (wasMain) {
            List<MenuImage> remain = menuImageRepository.findAllByMenu_MenuIdOrderByMenuImageIsMainDescMenuImageIdAsc(menuId);
            if (!remain.isEmpty()) {
                MenuImage nextMain = remain.get(0);
                clearMainImage(menu);
                nextMain.changeMain(true);
                menu.changeThumbnailUrl(nextMain.getMenuImageUrl());
            } else {
                menu.changeThumbnailUrl(null);
            }
        }
    }

    private void clearMainImage(Menu menu) {
        List<MenuImage> images = menuImageRepository.findAllByMenu_MenuId(menu.getMenuId());
        for (MenuImage image : images) {
            if (Boolean.TRUE.equals(image.getMenuImageIsMain())) {
                image.changeMain(false);
            }
        }
    }

    private String extractExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0) {
            return "";
        }
        return filename.substring(idx);
    }
}
