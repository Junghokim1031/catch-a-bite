package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.converter.CartItemConverter;
import com.deliveryapp.catchabite.dto.CartItemDTO;
import com.deliveryapp.catchabite.entity.Cart;
import com.deliveryapp.catchabite.entity.CartItem;
import com.deliveryapp.catchabite.entity.Menu;
import com.deliveryapp.catchabite.repository.CartItemRepository;
import com.deliveryapp.catchabite.repository.CartRepository;
import com.deliveryapp.catchabite.repository.MenuRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final MenuRepository menuRepository;
    private final CartItemConverter cartItemConverter;

    // [수정] quantity 파라미터 추가 반영
    // 사용자가 요청한 수량만큼 장바구니에 담기 위해 quantity 인자를 추가하고 로직에 반영함.
    @Override
    @Transactional
    public CartItemDTO createCartItem(Long cartId, Long menuId, Integer quantity) {
        // Parameter 확인
        // Long 및 Integer Null 체크
        if (cartId == null) {
            throw new IllegalArgumentException("카트Id가 null입니다. CartItemServiceImpl - createCartItem");
        }
        if (menuId == null) {
            throw new IllegalArgumentException("menuId가 null입니다. CartItemServiceImpl - createCartItem");
        }
        // quantity가 null이거나 0 이하라면 기본값 1로 설정
        int validQuantity = (quantity == null || quantity <= 0) ? 1 : quantity;

        // 카트 및 Menu를 build함.
        Cart cartRef = cartRepository.findById(cartId)
            .orElseThrow(() -> new IllegalArgumentException("카트가 존재하지 않습니다. \n" +
                "CartServiceImpl - createCartItem\ncartId=" + cartId));

        Menu menuRef = menuRepository.findById(menuId)
            .orElseThrow(() -> new IllegalArgumentException("메뉴가 존재하지 않습니다. \n" +
                "CartServiceImpl - createCartItem\nmenuId=" + menuId));

        // 이미 담긴 메뉴면 수량 합산, 없으면 생성
        // 같은 메뉴가 이미 장바구니에 있는지 확인
        CartItem existingItem = cartItemRepository.findByCart_CartIdAndMenu_MenuId(cartId, menuId).orElse(null);
        
        CartItem savedItem;
        if (existingItem != null) {
            // 이미 존재하면 수량 증가
            existingItem.changeQuantity(existingItem.getCartItemQuantity() + validQuantity);
            savedItem = existingItem;
        } else {
            // 없으면 새로 생성
            CartItem newItem = CartItem.builder()
                .cart(cartRef)
                .menu(menuRef)
                .cartItemQuantity(validQuantity) // 요청받은 수량 사용
                .build();
            savedItem = cartItemRepository.save(newItem);
        }
            
        CartItemDTO result = cartItemConverter.toDto(savedItem);
        
        // 로깅 하는 위치
        log.warn("==============================");
        log.warn(savedItem);
        log.warn(result);
        log.warn("==============================");

        // return
        return result;
    }

    @Override
    @Transactional
    public List<CartItemDTO> getCartItemsByCartId(Long cartId) {
        // Parameter 확인
        if (cartId == null){
            throw new IllegalArgumentException("cartId가 null입니다. CartItemServiceImpl - getCartItemsByCartId");
        }
        // List 생성
        List<CartItem> entities = cartItemRepository.findAllByCart_CartId(cartId);

        return entities.stream()
            .map(cartItemConverter::toDto)
            .toList();
    }


    @Override
    @Transactional
    public CartItemDTO updateCartItem(Long cartItemId, Integer newQuantity) {
        // Parameter 확인
        if (cartItemId == null) {
            throw new IllegalArgumentException("cartItemId가 null입니다. CartItemServiceImpl - updateCartItem");
        }
        if (newQuantity == null || newQuantity <= 0) {
            throw new IllegalArgumentException("newQuantity가 null 또는 0 이하입니다. CartItemServiceImpl - updateCartItem");
        }

        // cartItem 찾아오기
        CartItem cartItemRef = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "카트 아이템이 존재하지 않습니다.\nCartItemServiceImpl - updateCartItem\ncartItemId=" + cartItemId));
        
        // cartItem의 cartItemQuantity변경
        cartItemRef.changeQuantity(newQuantity);

        CartItemDTO result = cartItemConverter.toDto(cartItemRef);

        // Logging
        log.warn("==============================");
        log.warn("Entity: " + cartItemRef);
        log.warn("DTO: "+ result);
        log.warn("==============================");

        return result;
    }
    
    @Override
    @Transactional
    public void deleteCartItem(Long cartItemId) {
        // Parameter 확인
        if (cartItemId == null) {
            throw new IllegalArgumentException("cartItemId가 null입니다. CartItemServiceImpl - deleteCartItem");
        }

        // JPA사용하여 삭제
        cartItemRepository.deleteById(cartItemId);
    }
}