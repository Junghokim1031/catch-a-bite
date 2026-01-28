/* catchabite/controller/AppUserFavoriteController.java */
package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.dto.FavoriteStoreDTO;
import com.deliveryapp.catchabite.dto.UserFavoriteStoreResponseDTO;
import com.deliveryapp.catchabite.service.FavoriteStoreService;
import com.deliveryapp.catchabite.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appuser/favorites")
@RequiredArgsConstructor
public class AppUserFavoriteController {

    private final FavoriteStoreService favoriteStoreService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserFavoriteStoreResponseDTO>>> getMyFavorites(@AuthenticationPrincipal Object principal) {
        String loginKey = resolveLoginKey(principal);
        if (loginKey == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("UNAUTHORIZED", "로그인이 필요합니다."));
        }
        
        List<UserFavoriteStoreResponseDTO> favorites = favoriteStoreService.getMyFavoriteStores(loginKey);
        return ResponseEntity.ok(ApiResponse.ok(favorites));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FavoriteStoreDTO>> addFavorite(
            @AuthenticationPrincipal Object principal,
            @RequestBody FavoriteStoreDTO dto
    ) {
        String loginKey = resolveLoginKey(principal);
        if (loginKey == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("UNAUTHORIZED", "로그인이 필요합니다."));
        }

        FavoriteStoreDTO created = favoriteStoreService.addFavorite(dto.getStoreId(), loginKey);
        return ResponseEntity.ok(ApiResponse.ok(created));
    }

    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @PathVariable Long favoriteId
    ) {
        favoriteStoreService.removeFavorite(favoriteId);
        return ResponseEntity.ok(ApiResponse.ok(null,"즐겨찾기가 해제되었습니다."));
    }

    // Helper method to parse the "TYPE:LOGIN_ID" string from AuthController
    private String resolveLoginKey(Object principal) {
        if (principal instanceof String p && p.contains(":")) {
            // Split "USER:email@example.com" and take the second part
            return p.split(":", 2)[1];
        }
        return null;
    }
}