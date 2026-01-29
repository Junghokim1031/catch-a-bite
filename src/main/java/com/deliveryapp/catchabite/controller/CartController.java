package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.dto.CartItemDTO;
import com.deliveryapp.catchabite.dto.CartResponseDTO;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.entity.Cart;
import com.deliveryapp.catchabite.entity.CartItem;
import com.deliveryapp.catchabite.entity.Menu;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.repository.AppUserRepository;
import com.deliveryapp.catchabite.repository.CartRepository;
import com.deliveryapp.catchabite.repository.MenuRepository;
import com.deliveryapp.catchabite.security.AuthUser;
import com.deliveryapp.catchabite.service.CartItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 * [CartController]
 * 사용자의 장바구니 관련 요청을 처리하는 컨트롤러입니다.
 * - 장바구니 조회 (가게 정보 포함)
 * - 장바구니 아이템 추가 (단일 가게 정책 적용)
 * - 수량 변경 및 삭제
 * ============================================================
 */

@RestController
@RequestMapping("/api/v1/appuser/cart")
@RequiredArgsConstructor
@Log4j2
public class CartController {

    private final CartItemService cartItemService;
    private final CartRepository cartRepository;
    private final MenuRepository menuRepository;
    private final AppUserRepository appUserRepository;

    /**
     * ============================================================
     * [API: GET /api/v1/appuser/cart/my]
     * 목적: 현재 로그인한 사용자의 장바구니 정보를 상세 조회합니다.
     * 로직:
     * 1. 사용자의 가장 최근 장바구니(Cart)를 조회합니다.
     * 2. 장바구니가 없거나 비어있으면 null을 담아 반환합니다.
     * 3. 장바구니가 존재하면, 해당 장바구니의 가게 정보와 아이템 목록을 조합하여
     * CartResponseDTO로 변환 후 반환합니다.
     * ============================================================
     */

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<CartResponseDTO>> getMyCart(@AuthenticationPrincipal Object principal) {
        Long userId = getUserIdFromPrincipal(principal);

        // Find latest cart
        Cart cart = cartRepository.findFirstByAppUser_AppUserIdOrderByCartIdDesc(userId)
                .orElse(null);

        // 장바구니가 없거나 아이템이 하나도 없는 경우 처리
        if (cart == null || cart.getCartItems().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.ok(null));
        }

        Store store = cart.getStore();
        List<CartItem> entities = cart.getCartItems();

        // Entity 리스트를 DTO 리스트로 변환하며 메뉴 이미지, 가격 정보를 매핑
        List<CartItemDTO> itemDTOs = entities.stream().map(item -> {
            Menu menu = item.getMenu();
            String imageUrl = (menu.getMenuImage() != null) ? menu.getMenuImage().getMenuImageUrl() : null;
            long menuPrice = menu.getMenuPrice();

            // 옵션 정보 추출 및 포맷팅
            // 예: "치즈 추가 (+500원)" 또는 가격이 0원이면 "매운맛"
            List<String> optionStrings = item.getCartItemOptions().stream()
                .map(cartItemOption -> {
                    String name = cartItemOption.getMenuOption().getMenuOptionName();
                    Integer price = cartItemOption.getMenuOption().getMenuOptionPrice();
                    return price > 0 ? String.format("%s (+%d원)", name, price) : name;
                })
                .collect(Collectors.toList());
            
            // 옵션 가격을 포함하여 총 가격 계산
            // 정확한 계산을 위해선 옵션 가격 합산 로직이 필요하지만, 여기서는 UI 표시용 DTO 매핑에 집중합니다.
            // (필요 시 totalItemPrice 계산 로직에 옵션 가격 합산 추가 권장)
            long itemBasePrice = menuPrice;
            for(var opt : item.getCartItemOptions()) {
                itemBasePrice += opt.getMenuOption().getMenuOptionPrice();
            }

            return CartItemDTO.builder()
                    .cartItemId(item.getCartItemId())
                    .cartId(cart.getCartId())
                    .menuId(menu.getMenuId())
                    .cartItemQuantity(item.getCartItemQuantity())
                    .menuName(menu.getMenuName())
                    .menuPrice(menuPrice)
                    .menuImageUrl(imageUrl)
                    .menuOptions(optionStrings)
                    .totalItemPrice(itemBasePrice * item.getCartItemQuantity())
                    .build();
        }).collect(Collectors.toList());

        // 총 주문 음식 가격 계산
        Long totalFoodPrice = itemDTOs.stream().mapToLong(CartItemDTO::getTotalItemPrice).sum();

        // 최종 응답 DTO 생성
        CartResponseDTO response = CartResponseDTO.builder()
                .cartId(cart.getCartId())
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .deliveryCost(store.getStoreDeliveryFee() != null ? store.getStoreDeliveryFee().longValue() : 0L)
                .minOrderPrice(store.getStoreMinOrder() != null ? store.getStoreMinOrder().longValue() : 0L)
                .items(itemDTOs)
                .totalFoodPrice(totalFoodPrice)
                .build();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * ============================================================
     * [API: GET /api/v1/appuser/cart/{cartId}/items]
     * 목적: 특정 장바구니의 아이템 목록만 간단히 조회합니다.
     * ============================================================
     */
    @GetMapping("/{cartId}/items")
    public ResponseEntity<ApiResponse<List<CartItemDTO>>> getCartItems(@PathVariable Long cartId) {
        List<CartItemDTO> items = cartItemService.getCartItemsByCartId(cartId);
        return ResponseEntity.ok(ApiResponse.ok(items));
    }

    /**
     * ============================================================
     * [API: POST /api/v1/appuser/cart/items]
     * 목적: 장바구니에 메뉴를 추가합니다.
     * 정책: '단일 가게 장바구니(Single Cart Policy)'를 따릅니다.
     * - 사용자는 한 번에 하나의 가게에서만 주문할 수 있습니다.
     * - 다른 가게의 메뉴를 담으려고 하면, 기존 장바구니는 삭제되고 새 장바구니가 생성됩니다.
     * 로직:
     * 1. 사용자의 모든 장바구니를 조회합니다.
     * 2. 담으려는 메뉴의 가게와 일치하는 장바구니가 있는지 확인합니다.
     * - 일치(Same Store): 기존 장바구니(cart)를 재사용합니다.
     * - 불일치(Diff Store): 기존 장바구니를 삭제(delete)합니다.
     * 3. 장바구니가 없으면 새로 생성(save)합니다.
     * 4. CartItemService를 통해 아이템을 추가하거나 수량을 증가시킵니다.
     * ============================================================
     */
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartItemDTO>> addItemToCart(
            @AuthenticationPrincipal Object principal,
            @RequestBody CartItemDTO cartItemDTO
    ) {
        Long userId = getUserIdFromPrincipal(principal);
        Long menuId = cartItemDTO.getMenuId();
        Integer quantity = cartItemDTO.getCartItemQuantity();
        List<Long> optionIds = cartItemDTO.getOptionIds();

        // 메뉴 정보 검증
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu does not exist."));
        Store store = menu.getStore();

        // 해당 유저의 모든 카트 조회 (보통 1개 또는 0개여야 정상)
        List<Cart> userCarts = cartRepository.findAllByAppUser_AppUserId(userId);
        Cart cart = null;

        for (Cart c : userCarts) {
            // 기존 장바구니의 가게 ID와 현재 담으려는 메뉴의 가게 ID 비교
            if (c.getStore().getStoreId().equals(store.getStoreId())) {
                cart = c; // 같은 가게면 재사용
            } else {
                cartRepository.delete(c); // 다른 가게면 기존 장바구니 삭제
            }
        }

        // 재사용할 장바구니가 없으면 새로 생성
        if (cart == null) {
            AppUser appUser = appUserRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User info error"));
            Cart newCart = Cart.builder()
                    .appUser(appUser)
                    .store(store)
                    .build();
            cart = cartRepository.save(newCart);
        }

        CartItemDTO item = cartItemService.createCartItem(cart.getCartId(), menuId, quantity, optionIds);
        return ResponseEntity.ok(ApiResponse.ok(item));
    }

    /**
     * ============================================================
     * [API: PATCH /api/v1/appuser/cart/items/{cartItemId}]
     * 목적: 장바구니에 담긴 특정 아이템의 수량을 변경합니다.
     * ============================================================
     */
    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartItemDTO>> updateCartItemQuantity(@PathVariable Long cartItemId, @RequestBody CartItemDTO dto) {
        CartItemDTO item = cartItemService.updateCartItem(cartItemId, dto.getCartItemQuantity());
        return ResponseEntity.ok(ApiResponse.ok(item));
    }

    /**
     * ============================================================
     * [API: DELETE /api/v1/appuser/cart/items/{cartItemId}]
     * 목적: 장바구니에서 특정 아이템을 삭제합니다.
     * 참고: 아이템이 모두 삭제되어도 Cart 엔티티 자체는 DB에 남아있을 수 있습니다.
     * (이는 addItemToCart에서 재사용될 수 있게 합니다)
     * ============================================================
     */
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> deleteCartItem(@PathVariable Long cartItemId) {
        cartItemService.deleteCartItem(cartItemId);
        return ResponseEntity.ok(ApiResponse.okMessage("Item removed from cart"));
    }

    /**
     * ============================================================
     * [Helper Method]
     * 인증 객체(Principal)로부터 userId를 안전하게 추출합니다.
     * ============================================================
     */
    private Long getUserIdFromPrincipal(Object principal) {
        if (principal instanceof AuthUser) {
            return ((AuthUser) principal).getUserId();
        }
        if (principal instanceof String) {
            String principalStr = (String) principal;
            String[] parts = principalStr.split(":");
            if (parts.length >= 2) {
                String loginKey = parts[1].trim();
                return appUserRepository.findByAppUserEmailOrAppUserMobile(loginKey, loginKey)
                        .map(AppUser::getAppUserId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found for key: " + loginKey));
            }
        }
        throw new IllegalArgumentException("Invalid authentication principal");
    }
}