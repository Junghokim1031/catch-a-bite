package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.converter.CartItemConverter;
import com.deliveryapp.catchabite.dto.CartItemDTO;
import com.deliveryapp.catchabite.entity.Cart;
import com.deliveryapp.catchabite.entity.CartItem;
import com.deliveryapp.catchabite.entity.CartItemOption;
import com.deliveryapp.catchabite.entity.Menu;
import com.deliveryapp.catchabite.entity.MenuOption;
import com.deliveryapp.catchabite.repository.CartItemRepository;
import com.deliveryapp.catchabite.repository.CartRepository;
import com.deliveryapp.catchabite.repository.MenuOptionRepository;
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
    private final MenuOptionRepository menuOptionRepository;
    private final CartItemConverter cartItemConverter;

    // 사용자가 요청한 수량만큼 장바구니에 담기 위해 quantity 인자를 추가하고 로직에 반영함.
    @Override
    @Transactional
    public CartItemDTO createCartItem(Long cartId, Long menuId, Integer quantity, List<Long> optionIds) {
        // 1. 기본 유효성 검사
        if (cartId == null || menuId == null) throw new IllegalArgumentException("필수값 누락");
        int validQuantity = (quantity == null || quantity <= 0) ? 1 : quantity;

        Cart cartRef = cartRepository.findById(cartId)
            .orElseThrow(() -> new IllegalArgumentException("장바구니 없음"));
        Menu menuRef = menuRepository.findById(menuId)
            .orElseThrow(() -> new IllegalArgumentException("메뉴 없음"));

        // 2. 같은 메뉴라도 옵션이 다르면 별개의 아이템으로 봐야 함
        // 기존의 findByCart_CartIdAndMenu_MenuId 로직은 옵션을 고려하지 않으므로, 
        // 옵션이 있는 경우 '무조건 새로 생성'하는 것이 안전합니다.        
        CartItem newItem = CartItem.builder()
            .cart(cartRef)
            .menu(menuRef)
            .cartItemQuantity(validQuantity)
            .build();
        
        // 3. 옵션 저장 로직
        if (optionIds != null && !optionIds.isEmpty()) {
            List<MenuOption> options = menuOptionRepository.findAllById(optionIds);
            
            for (MenuOption option : options) {
                CartItemOption cartItemOption = CartItemOption.builder()
                        .cartItem(newItem) // 부모 설정
                        .menuOption(option)
                        .build();
                newItem.addOption(cartItemOption); // CascadeType.ALL에 의해 함께 저장됨
            }
        }

        // 4. 저장 후 반환
        // 함수 내 에서 생성하기 때문에 null 막음.
        @SuppressWarnings("null")
        CartItem savedItem = cartItemRepository.save(newItem);
        return cartItemConverter.toDto(savedItem);
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