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

/**
 * ============================================================
 * [AppUserFavoriteController]
 * 사용자의 '즐겨찾기(찜)' 기능을 관리하는 컨트롤러입니다.
 * - 즐겨찾기 목록 조회
 * - 새로운 가게 즐겨찾기 추가
 * - 기존 즐겨찾기 해제(삭제)
 * ============================================================
 */

@RestController
@RequestMapping("/api/v1/appuser/favorites")
@RequiredArgsConstructor
public class AppUserFavoriteController {

    private final FavoriteStoreService favoriteStoreService;

    /**
     * ============================================================
     * [함수의 목적] 
     * 현재 로그인한 사용자의 모든 즐겨찾기 가게 목록을 조회합니다.
     * GET /api/v1/appuser/favorites
     * ============================================================
     */
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

    /**
     * ============================================================
     * [함수의 목적] 
     * 특정 가게를 사용자의 즐겨찾기 목록에 새로 등록합니다.
     * * [API 호출 구조] 
     * POST /api/v1/appuser/favorites
     * Body: { "storeId": Long }
     * ============================================================
     */
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

    /**
     * ============================================================
     * [함수의 목적] 
     * 등록된 즐겨찾기 항목을 삭제하여 찜을 해제합니다.
     * * [API 호출 구조] 
     * DELETE /api/v1/appuser/favorites/{favoriteId}
     * ============================================================
     */
    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @PathVariable Long favoriteId
    ) {
        favoriteStoreService.removeFavorite(favoriteId);
        return ResponseEntity.ok(ApiResponse.ok(null,"즐겨찾기가 해제되었습니다."));
    }

    /**
     * ============================================================
     * [목적] Security Context의 Principal 객체에서 사용자 식별 문자열을 추출합니다.
     * [로직 상세] 
     * "ROLE:EMAIL" 형식(예: "USER:test@example.com")으로 들어오는 
     * 문자열에서 실제 로그인 아이디인 이메일 부분만 분리하여 반환합니다.
     * ============================================================
     */
    private String resolveLoginKey(Object principal) {
        if (principal instanceof String p && p.contains(":")) {
            // Split "USER:email@example.com" and take the second part
            return p.split(":", 2)[1];
        }
        return null;
    }
}