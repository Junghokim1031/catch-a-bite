package com.deliveryapp.catchabite.service;

import java.util.List;

import com.deliveryapp.catchabite.dto.CartItemDTO;

public interface CartItemService{
    // 기초 CRUD
    public CartItemDTO createCartItem(Long cartId, Long menuId, Integer quantity);
    public List<CartItemDTO> getCartItemsByCartId(Long cartId);
    public CartItemDTO updateCartItem(Long cartItemId, Integer quantity);
    public void deleteCartItem(Long cartItemId);
}
