package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.dto.CartItemDTO;
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
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/appuser/cart")
@RequiredArgsConstructor
@Log4j2
public class CartController {

    private final CartItemService cartItemService;
    private final CartRepository cartRepository;
    private final MenuRepository menuRepository;
    private final AppUserRepository appUserRepository;

    // [Added] Response DTO for the Cart Page
    @Data
    @Builder
    public static class CartResponseDTO {
        private Long cartId;
        private Long storeId;
        private String storeName;
        private Long deliveryCost;
        private Long minOrderPrice;
        private List<CartItemDTO> items;
        private Long totalFoodPrice;
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<CartResponseDTO>> getMyCart(@AuthenticationPrincipal Object principal) {
        Long userId = getUserIdFromPrincipal(principal);

        // Find latest cart
        Cart cart = cartRepository.findFirstByAppUser_AppUserIdOrderByCartIdDesc(userId)
                .orElse(null);

        if (cart == null || cart.getCartItems().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.ok(null)); // Empty cart
        }

        Store store = cart.getStore();
        List<CartItem> entities = cart.getCartItems();

        // Convert to DTO with Menu Details & Image
        List<CartItemDTO> itemDTOs = entities.stream().map(item -> {
            Menu menu = item.getMenu();
            String imageUrl = (menu.getMenuImage() != null) ? menu.getMenuImage().getMenuImageUrl() : null;
            long menuPrice = menu.getMenuPrice();

            return CartItemDTO.builder()
                    .cartItemId(item.getCartItemId())
                    .cartId(cart.getCartId())
                    .menuId(menu.getMenuId())
                    .cartItemQuantity(item.getCartItemQuantity())
                    .menuName(menu.getMenuName())
                    .menuPrice(menuPrice)
                    .menuImageUrl(imageUrl)
                    .totalItemPrice(menuPrice * item.getCartItemQuantity())
                    .build();
        }).collect(Collectors.toList());

        Long totalFoodPrice = itemDTOs.stream().mapToLong(CartItemDTO::getTotalItemPrice).sum();

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

    @GetMapping("/{cartId}/items")
    public ResponseEntity<ApiResponse<List<CartItemDTO>>> getCartItems(@PathVariable Long cartId) {
        List<CartItemDTO> items = cartItemService.getCartItemsByCartId(cartId);
        return ResponseEntity.ok(ApiResponse.ok(items));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartItemDTO>> addItemToCart(
            @AuthenticationPrincipal Object principal,
            @RequestBody CartItemDTO cartItemDTO
    ) {
        Long userId = getUserIdFromPrincipal(principal);
        Long menuId = cartItemDTO.getMenuId();
        Integer quantity = cartItemDTO.getCartItemQuantity();

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu does not exist."));
        Store store = menu.getStore();

        // [Modified] Enforce single cart policy:
        // If a cart exists for a different store, delete it.
        List<Cart> userCarts = cartRepository.findAllByAppUser_AppUserId(userId);
        Cart cart = null;

        for (Cart c : userCarts) {
            if (c.getStore().getStoreId().equals(store.getStoreId())) {
                cart = c;
            } else {
                cartRepository.delete(c);
            }
        }

        if (cart == null) {
            AppUser appUser = appUserRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User info error"));
            Cart newCart = Cart.builder()
                    .appUser(appUser)
                    .store(store)
                    .build();
            cart = cartRepository.save(newCart);
        }

        CartItemDTO item = cartItemService.createCartItem(cart.getCartId(), menuId, quantity);
        return ResponseEntity.ok(ApiResponse.ok(item));
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartItemDTO>> updateCartItemQuantity(@PathVariable Long cartItemId, @RequestBody CartItemDTO dto) {
        CartItemDTO item = cartItemService.updateCartItem(cartItemId, dto.getCartItemQuantity());
        return ResponseEntity.ok(ApiResponse.ok(item));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> deleteCartItem(@PathVariable Long cartItemId) {
        cartItemService.deleteCartItem(cartItemId);
        return ResponseEntity.ok(ApiResponse.okMessage("Item removed from cart"));
    }

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