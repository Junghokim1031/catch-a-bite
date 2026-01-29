package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.converter.StoreConverter;
import com.deliveryapp.catchabite.converter.StoreOrderConverter;
import com.deliveryapp.catchabite.dto.StoreOrderDTO;
import com.deliveryapp.catchabite.dto.UserStoreOrderRequestDTO;
import com.deliveryapp.catchabite.dto.UserStoreSummaryDTO;
import com.deliveryapp.catchabite.entity.*;
import com.deliveryapp.catchabite.repository.*;
import com.deliveryapp.catchabite.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final CartRepository cartRepository;
    private final PaymentRepository paymentRepository;
    
    private final StoreOrderConverter storeOrderConverter;
    private final StoreConverter storeConverter;

    // =====================================================================
    // [기능: 주문 생성]
    // 사용자의 장바구니 내용을 바탕으로 새로운 주문(StoreOrder)을 생성합니다.
    // 1. 필수 데이터(유저, 가게, 주소) 검증
    // 2. 장바구니 조회 및 가격 계산
    // 3. 주문 엔티티 및 결제 정보 생성/저장
    // 4. 장바구니 비우기
    // =====================================================================
    @Override
    @Transactional
    public StoreOrderDTO createStoreOrder(UserStoreOrderRequestDTO dto) {

        // =====================================================================
        // [로깅] 요청 정보 확인
        // 디버깅을 위해 주문을 요청한 사용자 ID와 대상 가게 ID를 로그에 남깁니다.
        // =====================================================================
        log.info("Order Request: UserID={}, StoreID={}", dto.getAppUserId(), dto.getStoreId());
        
        // =====================================================================
        // [단계 1] 필수 엔티티 조회 및 검증
        // 주문 생성에 반드시 필요한 AppUser(사용자), Store(가게), Address(배송지) 정보를 DB에서 찾습니다.
        // 만약 ID에 해당하는 데이터가 없다면 예외(Exception)를 발생시켜 로직을 중단합니다.
        // =====================================================================
        AppUser appUser = appUserRepository.findById(dto.getAppUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: ID " + dto.getAppUserId()));
        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("Store not found: ID " + dto.getStoreId()));
        Address address = addressRepository.findById(dto.getAddressId())
                .orElseThrow(() -> new IllegalArgumentException("Address not found: ID " + dto.getAddressId()));

        // =====================================================================
        // [단계 2] 장바구니(Cart) 조회
        // 해당 사용자가 해당 가게에 담아둔 장바구니 데이터를 가져옵니다.
        // 장바구니가 없거나 비어있다면 주문을 진행할 수 없으므로 예외 처리합니다.
        // =====================================================================
        Cart cart = cartRepository.findByAppUser_AppUserIdAndStore_StoreId(dto.getAppUserId(), dto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for this user and store"));
        
        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty. Cannot create order.");
        }

        // =====================================================================
        // [단계 3] 주문 항목(OrderItem) 생성 및 총 금액 계산
        // 장바구니에 담긴 각 메뉴(CartItem)를 반복문으로 돌면서 '주문 항목(OrderItem)'으로 변환합니다.
        // 동시에 (단가 x 수량)을 계산하여 총 음식 금액(totalFoodPrice)을 산출합니다.
        // =====================================================================
        List<OrderItem> orderItems = new ArrayList<>();
        long totalFoodPrice = 0L;
        
        for (CartItem cartItem : cartItems) {
            long price = cartItem.getMenu().getMenuPrice().longValue(); // 메뉴 단가
            long qty = cartItem.getCartItemQuantity().longValue();      // 주문 수량
            totalFoodPrice += (price * qty);                            // 총액 누적

            // OrderItem 객체를 생성합니다.
            // 주의: 아직 부모 객체인 storeOrder는 생성되지 않았으므로 null로 두거나 나중에 연결합니다.
            // 여기서는 빌더 패턴을 사용하여 데이터를 채워넣습니다.
            orderItems.add(OrderItem.builder()
                    .storeOrder(null) // 아래 [단계 4]에서 연결됨
                    .orderItemName(cartItem.getMenu().getMenuName())
                    .orderItemPrice(price)
                    .orderItemQuantity(qty)
                    .build());
        }

        // 최종 금액 = 음식 총액 + 가게 배달팁
        long finalTotalPrice = totalFoodPrice + store.getStoreDeliveryFee();
        // Store 내 배달비는 Integer이며 StoreOrder에는 Long이라 변경
        long finalDeliveryFee = store.getStoreDeliveryFee();

        // =====================================================================
        // [단계 4] 주문(StoreOrder) 객체 생성 및 DB 저장
        // 위에서 준비한 데이터들을 조합하여 실제 주문 엔티티를 생성합니다.
        // 참고: storeRequest(사장님 요청)와 riderRequest(기사님 요청)는 
        // 엔티티에 필드가 없으므로 DB에 저장되지 않습니다. (WebSocket 전송용)
        // =====================================================================
        StoreOrder finalOrder = StoreOrder.builder()
                .appUser(appUser)
                .store(store)
                .address(address)
                .orderAddressSnapshot(address.getAddressDetail()) // 주소가 변경될 수 있으므로 현재 주소를 스냅샷(문자열)으로 저장
                .orderDeliveryFee(finalDeliveryFee)
                .orderTotalPrice(finalTotalPrice)
                .orderStatus(com.deliveryapp.catchabite.domain.enumtype.OrderStatus.PAYMENTINPROGRESS) // 초기 상태: 대기중
                .orderDate(LocalDateTime.now()) // 주문 시각: 현재 시간
                .orderItems(new ArrayList<>())  // 빈 리스트로 초기화 (아래에서 추가됨)
                .build();
        
        // 생성된 주문 객체(finalOrder)를 각 주문 항목(OrderItem)에 연결합니다.
        // 이것이 없으면 OrderItem이 어떤 주문에 속하는지 DB가 알 수 없습니다.
        for(OrderItem item : orderItems) {
            item.setStoreOrder(finalOrder);
        }
        
        // 주문 객체 내부의 리스트에도 항목들을 추가합니다.
        // CascadeType.ALL 설정 덕분에 finalOrder만 저장해도 items가 같이 저장됩니다.
        finalOrder.getOrderItems().addAll(orderItems);

        // DB에 저장 (INSERT 쿼리 발생)
        StoreOrder savedOrder = storeOrderRepository.save(finalOrder);

        // =====================================================================
        // [단계 5] 결제(Payment) 정보 생성
        // 사용자가 선택한 결제 수단(paymentMethod)을 바탕으로 결제 데이터를 생성합니다.
        // 실제 PG사 승인은 별도 프로세스이므로, 여기서는 초기 상태인 'PENDING'으로 저장합니다.
        // =====================================================================
        if (dto.getPaymentMethod() != null) {
            Payment payment = Payment.builder()
                    .storeOrder(savedOrder)         // 위에서 저장한 주문과 연결
                    .paymentMethod(dto.getPaymentMethod())
                    .paymentAmount(finalTotalPrice)
                    .paymentStatus("PENDING")
                    .paymentPaidAt(LocalDateTime.now()) // 임시 시간
                    .build();
            paymentRepository.save(payment);
        }

        // =====================================================================
        // [단계 6] (TODO) 웹소켓 메시지 전송
        // 가게 사장님에게 실시간 주문 알림을 보내는 코드가 위치할 곳입니다.
        // DB에 저장되지 않은 요청사항(storeRequest, riderRequest)은 이 시점에 전송되어야 합니다.
        // =====================================================================
        // log.info("WS Message: StoreReq={}, RiderReq={}", dto.getStoreRequest(), dto.getRiderRequest());

        // =====================================================================
        // [단계 7] 장바구니 비우기
        // 주문이 성공적으로 생성되었으므로, 더 이상 필요 없는 장바구니 항목을 삭제합니다.
        // =====================================================================
        cartRepository.delete(cart);

        // 최종적으로 생성된 주문 정보를 DTO로 변환하여 반환합니다.
        return storeOrderConverter.toDto(savedOrder);
    }

    // =====================================================================
    // [기능: 주문 단건 조회]
    // 주문 ID(orderId)를 통해 특정 주문의 상세 정보를 조회합니다.
    // =====================================================================
    @Override
    public StoreOrderDTO getStoreOrder(Long storeOrderId) {
        StoreOrder order = storeOrderRepository.findById(storeOrderId)
            .orElseThrow(() -> new IllegalArgumentException("StoreOrder not found: " + storeOrderId));
        return storeOrderConverter.toDto(order);
    }

    // =====================================================================
    // [기능: 내 주문 목록 조회]
    // 특정 사용자(AppUser)가 주문한 모든 내역을 조회합니다.
    // @Transactional(readOnly = true)를 사용하여 조회 성능을 최적화합니다.
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
    // [기능: 전체 주문 조회 (관리자용)]
    // 시스템상의 모든 주문을 조회합니다. 데이터가 많을 경우 페이징 처리가 필요할 수 있습니다.
    // =====================================================================
    @Override
    public List<StoreOrderDTO> getAllStoreOrders() {
        return storeOrderRepository.findAll().stream()
                .map(storeOrderConverter::toDto)
                .collect(Collectors.toList());
    }

    // =====================================================================
    // [기능: 주문 정보 수정]
    // =====================================================================
    @Override
    @Transactional
    public StoreOrderDTO updateStoreOrder(Long orderId, StoreOrderDTO dto) {
        StoreOrder order = storeOrderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. ID: " + orderId));

        if (dto.getOrderStatus() != null) {
            log.info("주문 상태 변경 요청: OrderID={}, 기존상태={}, 변경상태={}", 
                    orderId, order.getOrderStatus(), dto.getOrderStatus());            
            order.changeStatus(dto.getOrderStatus());
        }
        
        return storeOrderConverter.toDto(order);
    }

    // =====================================================================
    // [기능: 주문 취소/삭제]
    // 주문 ID를 받아 DB에서 삭제합니다.
    // =====================================================================
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

    // =====================================================================
    // [기능: 자주 방문한 매장 조회]
    // 사용자의 주문 내역을 분석하여 가장 많이 주문한 매장 Top 10을 반환합니다.
    // =====================================================================
    @Override
    @Transactional(readOnly = true)
    public List<UserStoreSummaryDTO> getFrequentStores(Long userId) {
        List<Store> stores = storeOrderRepository.findMostFrequentStores(userId, PageRequest.of(0, 10));
        return stores.stream()
                .map(storeConverter::toSummaryDTO)
                .collect(Collectors.toList());
    }

    // =====================================================================
    // [기능: 리뷰 작성을 위한 주문 검증]
    // 리뷰를 작성하려는 주문이 실제로 존재하는지 확인하고 해당 주문 객체를 반환합니다.
    // =====================================================================
    @Override
    public StoreOrder getValidatedOrder(Long storeOrderId) {
        if (!storeOrderRepository.existsById(storeOrderId)) {
            log.error("Order validation failed. OrderId={} not found.", storeOrderId);
            throw new IllegalArgumentException("Order not found: " + storeOrderId);
        }
        return storeOrderRepository.findByOrderId(storeOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + storeOrderId));
    }

    // =====================================================================
    // [헬퍼 메서드: ID 추출]
    // 주문 객체에서 안전하게 가게 ID와 주소 ID를 추출하는 메서드들입니다.
    // NullPointerException을 방지하기 위해 null 체크를 수행합니다.
    // =====================================================================
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

    private Long safeExtractStoreId(StoreOrder order) {
        return (order.getStore() != null) ? order.getStore().getStoreId() : null;
    }

    private Long safeExtractAddressId(StoreOrder order) {
        return (order.getAddress() != null) ? order.getAddress().getAddressId() : null;
    }
}