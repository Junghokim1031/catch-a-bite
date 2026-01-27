/* catchabite/controller/AppUserStoreController.java */
package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.dto.MenuCategoryWithMenusDTO;
import com.deliveryapp.catchabite.dto.UserStoreSummaryDTO;
import com.deliveryapp.catchabite.dto.UserStoreResponseDTO;
import com.deliveryapp.catchabite.service.UserStoreService;
import com.deliveryapp.catchabite.service.UserMenuCategoryService;
import com.deliveryapp.catchabite.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/v1/appuser/stores")
@RequiredArgsConstructor
public class AppUserStoreController {

    private final UserStoreService userStoreService;
    private final UserMenuCategoryService userMenuCategoryService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserStoreSummaryDTO>>> searchStores(@RequestParam String keyword) {
        List<UserStoreSummaryDTO> stores = userStoreService.searchStores(keyword);
        return ResponseEntity.ok(ApiResponse.ok(stores));
    }

    @GetMapping("/category")
    public ResponseEntity<ApiResponse<List<UserStoreSummaryDTO>>> getStoresByCategory(@RequestParam String storeCategory) {
        List<UserStoreSummaryDTO> stores = userStoreService.getStoresByCategory(storeCategory);
        return ResponseEntity.ok(ApiResponse.ok(stores));
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<UserStoreResponseDTO>> getStoreDetails(
            @PathVariable Long storeId,
            @AuthenticationPrincipal Object principal
    ) {
        // Resolve loginKey if logged in (returns null if not logged in)
        String loginKey = resolveLoginKey(principal);
        
        UserStoreResponseDTO response = userStoreService.getStoreDetailsForUser(storeId, loginKey);
        
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{storeId}/menus")
    public ResponseEntity<ApiResponse<List<MenuCategoryWithMenusDTO>>> getStoreMenus(@PathVariable Long storeId) {
        List<MenuCategoryWithMenusDTO> menus = userMenuCategoryService.getMenuBoardForUser(storeId);
        return ResponseEntity.ok(ApiResponse.ok(menus));
    }

    @GetMapping("/random")
    public ResponseEntity<ApiResponse<List<UserStoreSummaryDTO>>> getRandomStores() {
        List<UserStoreSummaryDTO> stores = userStoreService.getRandomStores();
        return ResponseEntity.ok(ApiResponse.ok(stores));
    }

    // Helper method to parse the "TYPE:LOGIN_ID" string
    private String resolveLoginKey(Object principal) {
        if (principal instanceof String p && p.contains(":")) {
            return p.split(":", 2)[1];
        }
        return null;
    }
}