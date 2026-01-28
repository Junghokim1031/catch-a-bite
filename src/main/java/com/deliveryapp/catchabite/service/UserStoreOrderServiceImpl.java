package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.converter.StoreConverter;
import com.deliveryapp.catchabite.converter.StoreOrderConverter;
import com.deliveryapp.catchabite.dto.StoreOrderDTO;
import com.deliveryapp.catchabite.dto.UserStoreSummaryDTO;
import com.deliveryapp.catchabite.entity.*;
import com.deliveryapp.catchabite.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserStoreOrderServiceImpl implements UserStoreOrderService {

    private final StoreOrderRepository storeOrderRepository;
    private final AppUserRepository appUserRepository;
    private final StoreRepository storeRepository;
    private final AddressRepository addressRepository;
    private final StoreOrderConverter storeOrderConverter;
    
    // [추가] 장바구니 데이터를 가져오기 위해 리포지토리 주입
    private final CartRepository cartRepository;
    private final StoreConverter storeConverter;

    // =====================================================================
    // CREATE (장바구니 기반 주문 생성)
    // =====================================================================
    @SuppressWarnings("null")
    @Override
    @Transactional
    public StoreOrderDTO createStoreOrder(StoreOrderDTO dto) {

        // =====================================================================
        // StoreOrder 생성에 필요한 자료가 존재하는지 확인
        // 사용자, 가게, 및 주소 
        // 이 자료들은 DB에서 확인해야 되서 따로 확인함.
        // 이외 정보는 프론트엔드에서 StoreOrderDTO 형식으로 받음.
        // =====================================================================


        log.info("=============================================");
        log.info("주문 생성 요청: UserID={}, StoreID={}", dto.getAppUserId(), dto.getStoreId());
        log.info("=============================================");

        // 1. 기본 엔티티 조회 (User, Store, Address)
        AppUser appUser = appUserRepository.findById(dto.getAppUserId())
            .orElseThrow(() -> new IllegalArgumentException("UserStoreOrderServiceImpl - AppUserId를 찾을 수 없습니다. ID: " + dto.getAppUserId()));
        
        Store store = storeRepository.findById(dto.getStoreId())
            .orElseThrow(() -> new IllegalArgumentException("UserStoreOrderServiceImpl - StoreId를 찾을 수 없습니다. ID: " + dto.getStoreId()));
        
        Address address = addressRepository.findById(dto.getAddressId())
            .orElseThrow(() -> new IllegalArgumentException("UserStoreOrderServiceImpl - AddressId를 찾을 수 없습니다. ID: " + dto.getAddressId()));


        // 2. 장바구니 조회 (User + Store 기준)
        Cart cart = cartRepository.findByAppUser_AppUserIdAndStore_StoreId(dto.getAppUserId(), dto.getStoreId())
            .orElseThrow(() -> new IllegalArgumentException("UserStoreOrderServiceImpl - 장바구니가 비어있거나 존재하지 않습니다."));

        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("UserStoreOrderServiceImpl - 장바구니에 주문할 메뉴가 없습니다.");
        }


        // 3. 주문(StoreOrder) 객체 생성 (아직 저장 전)
        // DTO에서 받은 기본 배송비 등을 포함, 초기 가격은 0원으로 설정 후 계산
        StoreOrder storeOrder = StoreOrder.builder()
                .appUser(appUser)
                .store(store)
                .address(address)
                .orderAddressSnapshot(address.getAddressDetail()) // 주소 스냅샷 저장
                .orderDeliveryFee(dto.getOrderDeliveryFee() != null ? dto.getOrderDeliveryFee() : 0L)
                .orderTotalPrice(0L) // 아래에서 계산하여 업데이트
                .build();


        // 4. 장바구니 항목(CartItem) -> 주문 항목(OrderItem) 변환 및 총액 계산
        long calculatedTotalPrice = 0L;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Menu menu = cartItem.getMenu();
            
            // 메뉴 가격(Integer) -> Long 변환
            long itemPrice = menu.getMenuPrice().longValue(); 
            long quantity = cartItem.getCartItemQuantity().longValue();
            long lineTotal = itemPrice * quantity;

            // OrderItem 생성 (Snapshot)
            OrderItem orderItem = OrderItem.builder()
                    .storeOrder(storeOrder) // 연관관계 설정
                    .orderItemName(menu.getMenuName())
                    .orderItemPrice(itemPrice)
                    .orderItemQuantity(quantity)
                    .build();

            orderItems.add(orderItem);
            calculatedTotalPrice += lineTotal;
        }

        // 5. 주문 객체에 아이템 리스트 및 최종 가격 설정
        storeOrder.getOrderItems().addAll(orderItems);

        // 총 결제 금액 = 음식 총액 + 배달팁
        long finalTotalPrice = calculatedTotalPrice + storeOrder.getOrderDeliveryFee();
        
        StoreOrder finalOrder = StoreOrder.builder()
            .appUser(appUser)
            .store(store)
            .address(address)
            .orderAddressSnapshot(address.getAddressDetail())
            .orderDeliveryFee(storeOrder.getOrderDeliveryFee())
            .orderStatus(com.deliveryapp.catchabite.domain.enumtype.OrderStatus.PENDING)
            .orderDate(java.time.LocalDateTime.now())
            .orderTotalPrice(finalTotalPrice)
            .orderItems(orderItems)
            .build();

        // 6. DB 저장 (Cascade.ALL로 인해 OrderItem들도 같이 저장됨)
        StoreOrder savedOrder = storeOrderRepository.save(finalOrder);

        // 7. 주문 완료 후 장바구니 비우기
        cartRepository.delete(cart);
        log.info("주문 완료 및 장바구니 삭제됨: OrderID={}", savedOrder.getOrderId());

        return storeOrderConverter.toDto(savedOrder);
    }

    // =====================================================================
    // READ
    // =====================================================================
    @SuppressWarnings("null")
    @Override
    public StoreOrderDTO getStoreOrder(Long storeOrderId) {
        StoreOrder order = storeOrderRepository.findById(storeOrderId)
            .orElseThrow(() -> new IllegalArgumentException("StoreOrderService - storeOrderId " + storeOrderId + "가 없음."));
        return storeOrderConverter.toDto(order);
    }

    // =====================================================================
    // READ ALL (AppUserId)
    // =====================================================================
    @Override
    @Transactional(readOnly = true)
    public List<StoreOrderDTO> getAllStoreOrdersForId(Long appUserId) {
        List<StoreOrder> orders = storeOrderRepository.findAllWithItemsByUserId(appUserId);

        return orders.stream()
                .map(storeOrderConverter::toDto)
                .collect(Collectors.toList());
    }

    // =====================================================================
    // READ ALL (Everything)
    // =====================================================================
    @Override
    public List<StoreOrderDTO> getAllStoreOrders() {
        return storeOrderRepository.findAll().stream()
                .map(storeOrderConverter::toDto)
                .collect(Collectors.toList());
    }

    // =====================================================================
    // UPDATE
    // =====================================================================
    @SuppressWarnings("null")
    @Override
    @Transactional
    public StoreOrderDTO updateStoreOrder(Long orderId, StoreOrderDTO dto) {
        StoreOrder order = storeOrderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("StoreOrderServiceImpl - updateStoreOrder - OrderId " + orderId + "가 존재하지 않습니다."));

        // 상태 변경 등 비즈니스 로직 필요 시 여기에 구현
        // 예: order.changeStatus(dto.getOrderStatus());
        
        return storeOrderConverter.toDto(order);
    }

    // =====================================================================
    // DELETE
    // =====================================================================
    @SuppressWarnings("null")
    @Override
    @Transactional
    public boolean deleteStoreOrder(Long orderId) {
        if (!storeOrderRepository.existsById(orderId)){
            return false;
        } 

        storeOrderRepository.deleteById(orderId);

        log.info("StoreOrder deleted: orderId={}", orderId);
        return true;
    }

    
    @Override
    @Transactional(readOnly = true)
    public List<UserStoreSummaryDTO> getFrequentStores(Long userId) {
        List<Store> stores = storeOrderRepository.findMostFrequentStores(userId, PageRequest.of(0, 10));
        return stores.stream()
                .map(storeConverter::toSummaryDTO)
                .collect(Collectors.toList());
    }

    // ===== Review에서 필요한 자료 =====

    @SuppressWarnings("null")
    @Override
    public StoreOrder getValidatedOrder(Long storeOrderId) {
        if (!storeOrderRepository.existsById(storeOrderId)) {
            log.error("UserStoreOrderService - getValidatedOrder - 주문 없음: orderId={}", 
                storeOrderId);
            throw new IllegalArgumentException("UserStoreOrderService - getValidatedOrder - 주문이 존재하지 않습니다: " + storeOrderId);
        }
        return storeOrderRepository.findByOrderId(storeOrderId)
                .orElseThrow(() -> new IllegalArgumentException("UserStoreOrderService - getValidatedOrder - 주문이 존재하지 않습니다: " + storeOrderId));
    }

    @Override
    public Long getStoreId(Long storeOrderId) {
        StoreOrder order = getValidatedOrder(storeOrderId);
        return safeExtractStoreId(order);
    }

    @Override
    public Long getAddressId(Long storeOrderId) {
        StoreOrder order = getValidatedOrder(storeOrderId);
        return safeExtractAddressId(order);
    }

    // ===== HELPER Methods =====
    private Long safeExtractStoreId(StoreOrder order) {
        return order.getStore() != null ? order.getStore().getStoreId() : null;
    }

    private Long safeExtractAddressId(StoreOrder order) {
        return order.getAddress() != null ? order.getAddress().getAddressId() : null;
    }
}