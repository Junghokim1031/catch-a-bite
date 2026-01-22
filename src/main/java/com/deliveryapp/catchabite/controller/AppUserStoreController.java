package com.deliveryapp.catchabite.controller;

/**
 * AppUserStoreController: 사용자용 매장 조회 HTTP 엔드포인트
 * * Description: 앱 사용자가 매장을 검색하거나, 카테고리별로 조회하고 상세 정보 및 메뉴를 확인합니다.
 * * 주요 기능:
 * 1. 매장 검색 (searchStores) - 키워드로 매장 검색 [GET, Return: List<StoreDTO>]
 * 2. 카테고리별 매장 조회 (getStoresByCategory) - 음식 카테고리별 매장 목록 조회 [GET, Return: List<StoreDTO>]
 * 3. 매장 상세 조회 (getStoreDetails) - 매장 기본 정보 조회 [GET, Return: StoreDTO]
 * 4. 매장 메뉴 조회 (getStoreMenus) - 매장의 메뉴 카테고리 및 메뉴 목록 조회 [GET, Return: List<MenuCategoryWithMenusDTO>]
 */

import com.deliveryapp.catchabite.converter.StoreConverter;
import com.deliveryapp.catchabite.dto.MenuCategoryWithMenusDTO;
import com.deliveryapp.catchabite.dto.StoreDTO;
import com.deliveryapp.catchabite.dto.StoreSummaryDTO;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.repository.StoreRepository;
import com.deliveryapp.catchabite.service.UserStoreService;
import com.deliveryapp.catchabite.service.UserMenuCategoryService;
import com.deliveryapp.catchabite.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/v1/appuser/stores")
@RequiredArgsConstructor
public class AppUserStoreController {

    private final UserStoreService userStoreService;
    private final StoreRepository storeRepository;
    private final StoreConverter storeConverter;
    private final UserMenuCategoryService userMenuCategoryService;



    /**
     * 가게 이름 또는 카테고리 키워드로 매장을 검색합니다.
     * GET /api/v1/appuser/stores/search?keyword={keyword}
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<StoreDTO>>> searchStores(@RequestParam String keyword) {
        List<StoreDTO> stores = userStoreService.searchStores(keyword);
        return ResponseEntity.ok(ApiResponse.ok(stores)); //
    }

    /**
     * 특정 카테고리에 해당하는 매장 목록을 조회합니다.
     * GET /api/v1/appuser/stores/category?name={storeCategory}
     */
    @GetMapping("/category")
    public ResponseEntity<ApiResponse<List<StoreDTO>>> getStoresByCategory(@RequestParam String storeCategory) {
        List<StoreDTO> stores = userStoreService.getStoresByCategory(storeCategory);
        return ResponseEntity.ok(ApiResponse.ok(stores)); //
    }

    /**
     * 특정 매장의 상세 정보를 조회합니다.
     * GET /api/v1/appuser/stores/{storeId}
     */
    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreDTO>> getStoreDetails(@PathVariable Long storeId) {
        // storeId에 문제가있으면 null을 반환함
        @SuppressWarnings("null")
        Store store = storeRepository.findById(storeId).orElse(null);
        if(store == null){
            return ResponseEntity.noContent().build();
        }
        StoreDTO storeDTO = storeConverter.toDto(store);
        return ResponseEntity.ok(ApiResponse.ok(storeDTO));
    }

    /**
     * 특정 매장의 전체 메뉴판(카테고리 및 메뉴)을 조회합니다.
     * GET /api/v1/appuser/stores/{storeId}/menus
     */
    @GetMapping("/{storeId}/menus")
    public ResponseEntity<ApiResponse<List<MenuCategoryWithMenusDTO>>> getStoreMenus(@PathVariable Long storeId) {
        
        List<MenuCategoryWithMenusDTO> menus = userMenuCategoryService.getMenuBoardForUser(storeId);
        if (menus.isEmpty()) {
            log.info("======================================================");
            log.info("메뉴분류가 없습니다");
            log.info("======================================================");
        }
        return ResponseEntity.ok(ApiResponse.ok(menus));
    }

    /**
     * 랜덤 가게 10개를 조회합니다.
     * 리액트에서 필요한 정보만 갖고 있는 StoreSummaryDTO를 반환합니다.
     * GET /api/v1/appuser/stores/random?limit=10
     */
    @GetMapping("/random")
    public ResponseEntity<ApiResponse<List<StoreSummaryDTO>>> getRandomStores(@RequestParam(defaultValue = "10") int limit) {
        List<StoreSummaryDTO> stores = userStoreService.getRandomStores(limit);
        return ResponseEntity.ok(ApiResponse.ok(stores));
    }
}