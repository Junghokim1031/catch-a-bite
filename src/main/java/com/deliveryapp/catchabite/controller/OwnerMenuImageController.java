package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.dto.MenuDTO;
import com.deliveryapp.catchabite.dto.MenuImageDTO;
import com.deliveryapp.catchabite.security.OwnerContext;
import com.deliveryapp.catchabite.service.MenuImageService;
import com.deliveryapp.catchabite.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owner/stores/{storeId}/menus")
public class OwnerMenuImageController {

    private final MenuService menuService;
    private final MenuImageService menuImageService;
    private final OwnerContext ownerContext;

    /**
     * 메뉴 등록 + 이미지 업로드(실무형 one-shot)
     * - multipart/form-data
     * - menu: MenuDTO(JSON)
     * - images: 이미지 파일(여러 장 가능, 첫 장을 대표 이미지로 처리)
     */
    @PostMapping(value = "/with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MenuDTO>> createMenuWithImages(
            Principal principal,
            @PathVariable Long storeId,
            @RequestPart("menu") MenuDTO menu,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

        MenuDTO created = menuService.createMenu(storeOwnerId, storeId, menu);

        if (images != null && !images.isEmpty()) {
            List<MenuImageDTO> uploaded =
                    menuImageService.uploadMenuImages(storeOwnerId, storeId, created.getMenuId(), images, true);
            if (!uploaded.isEmpty()) {
                // 첫 장을 대표 이미지로 저장하는 정책
                created.setMenuThumbnailUrl(uploaded.get(0).getMenuImageUrl());
            }
        }

        return ResponseEntity.ok(ApiResponse.ok(created));
    }

    // 메뉴 이미지 목록
    @GetMapping("/{menuId}/images")
    public ResponseEntity<ApiResponse<List<MenuImageDTO>>> getMenuImages(
            Principal principal,
            @PathVariable Long storeId,
            @PathVariable Long menuId
    ) {
        Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
        return ResponseEntity.ok(ApiResponse.ok(menuImageService.getMenuImages(storeOwnerId, storeId, menuId)));
    }

    // 메뉴 이미지 URL로 등록(외부 스토리지/프론트 업로드 사용 시)
    @PostMapping("/{menuId}/images")
    public ResponseEntity<ApiResponse<MenuImageDTO>> createMenuImageByUrl(
            Principal principal,
            @PathVariable Long storeId,
            @PathVariable Long menuId,
            @RequestBody MenuImageDTO dto
    ) {
        Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
        return ResponseEntity.ok(ApiResponse.ok(menuImageService.createMenuImageByUrl(storeOwnerId, storeId, menuId, dto)));
    }

    // 메뉴 이미지 파일 업로드
    @PostMapping(value = "/{menuId}/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<MenuImageDTO>>> uploadMenuImages(
            Principal principal,
            @PathVariable Long storeId,
            @PathVariable Long menuId,
            @RequestPart("images") List<MultipartFile> images,
            @RequestParam(required = false) Boolean setFirstAsMain
    ) {
        Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
        return ResponseEntity.ok(ApiResponse.ok(menuImageService.uploadMenuImages(storeOwnerId, storeId, menuId, images, setFirstAsMain)));
    }

    // 대표 이미지 변경
    @PatchMapping("/{menuId}/images/{menuImageId}/main")
    public ResponseEntity<ApiResponse<Object>> setMainImage(
            Principal principal,
            @PathVariable Long storeId,
            @PathVariable Long menuId,
            @PathVariable Long menuImageId
    ) {
        Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
        menuImageService.setMainImage(storeOwnerId, storeId, menuId, menuImageId);
        return ResponseEntity.ok(ApiResponse.ok(null, "menu main image updated"));
    }

    // 이미지 삭제
    @DeleteMapping("/{menuId}/images/{menuImageId}")
    public ResponseEntity<ApiResponse<Object>> deleteMenuImage(
            Principal principal,
            @PathVariable Long storeId,
            @PathVariable Long menuId,
            @PathVariable Long menuImageId
    ) {
        Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
        menuImageService.deleteMenuImage(storeOwnerId, storeId, menuId, menuImageId);
        return ResponseEntity.ok(ApiResponse.ok(null, "menu image deleted"));
    }
}
