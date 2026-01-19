package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.dto.CartItemDTO;
import com.deliveryapp.catchabite.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appuser/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartItemService cartItemService;

    @GetMapping("/{cartId}/items")
    public ResponseEntity<ApiResponse<List<CartItemDTO>>> getCartItems(@PathVariable Long cartId) {
        List<CartItemDTO> items = cartItemService.getCartItemsByCartId(cartId);
        return ResponseEntity.ok(ApiResponse.ok(items));
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<ApiResponse<CartItemDTO>> addItemToCart(@PathVariable Long cartId, @RequestParam Long menuId) {
        CartItemDTO item = cartItemService.createCartItem(cartId, menuId);
        return ResponseEntity.ok(ApiResponse.ok(item));
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartItemDTO>> updateCartItemQuantity(@PathVariable Long cartItemId, @RequestParam Integer quantity) {
        CartItemDTO item = cartItemService.updateCartItem(cartItemId, quantity);
        return ResponseEntity.ok(ApiResponse.ok(item));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> deleteCartItem(@PathVariable Long cartItemId) {
        cartItemService.deleteCartItem(cartItemId);
        return ResponseEntity.ok(ApiResponse.okMessage("Item removed from cart"));
    }
}