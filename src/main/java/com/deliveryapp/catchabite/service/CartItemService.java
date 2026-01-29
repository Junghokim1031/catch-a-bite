package com.deliveryapp.catchabite.service;

import java.util.List;

import com.deliveryapp.catchabite.dto.CartItemDTO;

public interface CartItemService{
    // 기초 CRUD
    CartItemDTO createCartItem(Long cartId, Long menuId, Integer quantity, List<Long> optionIds);
    List<CartItemDTO> getCartItemsByCartId(Long cartId);
    CartItemDTO updateCartItem(Long cartItemId, Integer quantity);
    void deleteCartItem(Long cartItemId);
}
