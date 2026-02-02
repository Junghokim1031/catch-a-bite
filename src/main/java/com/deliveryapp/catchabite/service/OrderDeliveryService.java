package com.deliveryapp.catchabite.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliveryapp.catchabite.domain.enumtype.OrderStatus;
import com.deliveryapp.catchabite.entity.StoreOrder;

import com.deliveryapp.catchabite.domain.enumtype.DeliveryStatus;
import com.deliveryapp.catchabite.dto.OrderDeliveryDTO;
import com.deliveryapp.catchabite.entity.Deliverer;
import com.deliveryapp.catchabite.entity.OrderDelivery;
import com.deliveryapp.catchabite.repository.DelivererRepository;
import com.deliveryapp.catchabite.repository.OrderDeliveryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderDeliveryService {

    private final OrderDeliveryRepository orderDeliveryRepository;
    private final DelivererRepository delivererRepository;

    @Transactional
    public void assignDeliverer(Long deliveryId, Long delivererId) {

        // // 1) 배달 조회
        OrderDelivery orderDelivery = orderDeliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("배달 요청이 없습니다. deliveryId=" + deliveryId));

        // // 2) 상태 검증(배정 가능 상태인지)
        // DeliveryStatus current = orderDelivery.getOrderDeliveryStatus();

        // // 이미 완료/취소는 배정 불가
        // if (current == DeliveryStatus.DELIVERED || current == DeliveryStatus.CANCELLED) {
        //     throw new IllegalStateException("배달원에게 배달 요청이 불가합니다. status=" + current);
        // }

        // // 이미 배정되어 있으면 재배정 정책 결정 필요 (여기선 불가)
        // if (orderDelivery.getDeliverer() != null) {
        //     throw new IllegalStateException("이미 배달원이 배정되어있습니다. deliveryId=" + deliveryId);
        // }

        // // 3) 배달원 조회
        // Deliverer deliverer = delivererRepository.findById(delivererId)
        //         .orElseThrow(() -> new IllegalArgumentException("배달원을 찾을 수 없습니다. delivererId=" + delivererId));

        // // 4) 배달원 상태 검증(예: 운행 가능 여부)
        // // deliverer_status가 YesNo.Y/N 같은 구조라면:
        // // if (deliverer.getStatus() != YesNo.Y) throw new IllegalStateException("Deliverer not available.");

        // // 5) 배정 + 상태 변경
        // orderDelivery.setDeliverer(deliverer);
        // orderDelivery.setOrderDeliveryStatus(DeliveryStatus.ASSIGNED);

        // // (선택) 배정 시간 필드가 따로 있다면 여기서 set
        // // orderDelivery.setAssignedAt(LocalDateTime.now());

        // // 6) save는 필수는 아님(dirty checking) but 명시해도 OK
        // // orderDeliveryRepository.save(orderDelivery);

        // 단계 축소: 배정 단계(ASSIGNED)는 사용하지 않음. 기본 상태는 PENDING 유지
        orderDelivery.setOrderDeliveryStatus(DeliveryStatus.PENDING);

    }

    @Transactional
    public void acceptByPrincipal(Long deliveryId, String principal) {
        throw new UnsupportedOperationException("accept 단계는 사용하지 않습니다. pickup-complete를 사용하세요.");
    }

    @Transactional
    public void accept(Long deliveryId, Long delivererId) {
        throw new UnsupportedOperationException("accept 단계는 사용하지 않습니다. pickup-complete를 사용하세요.");

        // // 배달 건 조회 (동시성 대비 : for update)
        // OrderDelivery od = orderDeliveryRepository.findByIdForUpdate(deliveryId)
        //         .orElseThrow(() -> new IllegalArgumentException("배달 요청이 없습니다. id=" + deliveryId));

        // // 배정 여부 확인
        // if (od.getDeliverer() == null) throw new IllegalStateException("배정된 배달원이 없습니다.");

        // // 배정된 배달원만 수락 가능
        // Long assignedDelivererId = od.getDeliverer().getDelivererId();
        // if (!assignedDelivererId.equals(delivererId)) throw new IllegalStateException("배정된 배달원이 아닙니다.");

        // // 상태 검증 (수락 가능한 상태인지)
        // // ASSIGNED - '배차요청(수락 대기)'된 주문이 아닐 경우 이전으로 되돌림.
        // if (od.getOrderDeliveryStatus() != DeliveryStatus.ASSIGNED) {
        //     // ACCEPTED(수락하기)는 주문이 ASSIGNED(배차요청)된 상태에서만 가능
        //     throw new IllegalStateException("배차요청된 주문만 수락할 수 있습니다. current=" + od.getOrderDeliveryStatus());
        // }

        // // 수락 시간 기록
        // od.setOrderAcceptTime(LocalDateTime.now());
        // // 배차 수락 상태로 변경됨
        // od.setOrderDeliveryStatus(DeliveryStatus.ACCEPTED);
    }

    @Transactional
    public void pickupCompleteByPrincipal(Long deliveryId, String principal) {
        Long delivererId = resolveDelivererIdFromPrincipal(principal);
        pickupComplete(deliveryId, delivererId);
    }

    @Transactional
    public void pickupComplete(Long deliveryId, Long delivererId) {

        OrderDelivery od = orderDeliveryRepository.findByIdForUpdate(deliveryId)
            .orElseThrow(() -> new IllegalArgumentException("배달이 없습니다."));

        if (od.getDeliverer() == null || !od.getDeliverer().getDelivererId().equals(delivererId)) {
            throw new SecurityException("본인 배달이 아닙니다.");
        }

        if (od.getOrderDeliveryStatus() != DeliveryStatus.PENDING) {
            throw new IllegalStateException("픽업 완료는 대기(PENDING) 상태에서만 가능합니다.");
        }

        StoreOrder so = od.getStoreOrder();
        if (so == null) throw new IllegalStateException("주문 정보가 없습니다.");

        if (so.getOrderStatus() != OrderStatus.COOKED) {
            throw new IllegalStateException("픽업 완료는 주문상태가 COOKED일 때만 가능합니다.");
        }

        od.setOrderDeliveryPickupTime(LocalDateTime.now());
        od.setOrderDeliveryStartTime(LocalDateTime.now());
        od.setOrderDeliveryStatus(DeliveryStatus.IN_DELIVERY);

        so.changeStatus(OrderStatus.DELIVERING);

        orderDeliveryRepository.save(od);
    }

    @Transactional
    public void startDeliveryByPrincipal(Long deliveryId, String principal) {
        throw new UnsupportedOperationException("start 단계는 사용하지 않습니다. pickup-complete를 사용하세요.");
    }

    @Transactional
    public void startDelivery(Long deliveryId, Long delivererId) {
        throw new UnsupportedOperationException("start 단계는 사용하지 않습니다. pickup-complete를 사용하세요.");

        // // 1) 배달 조회 (동시성 대비: for update 권장)
        // OrderDelivery od = orderDeliveryRepository.findByIdForUpdate(deliveryId)
        //         .orElseThrow(() -> new IllegalArgumentException("배달 요청이 없습니다. id=" + deliveryId));

        // // 2) 배정 여부 확인
        // if (od.getDeliverer() == null) {
        //     throw new IllegalStateException("배달원이 배정되지 않았습니다.");
        // }

        // // 3) 배정된 배달원인지 확인
        // if (!od.getDeliverer().getDelivererId().equals(delivererId)) { // PK getter에 맞게 수정
        //     throw new IllegalStateException("배정된 배달원이 아닙니다.");
        // }

        // // 4) 상태 검증: 픽업 완료된 건만 배달 시작 가능
        // if (od.getOrderDeliveryStatus() != DeliveryStatus.PICKED_UP) {
        //     throw new IllegalStateException(
        //             "픽업한 건에 대해서만 배달 가능합니다. current=" + od.getOrderDeliveryStatus());
        // }

        // // 5) 중복 시작 방지
        // if (od.getOrderDeliveryStartTime() != null) {
        //     throw new IllegalStateException("이미 시작된 배달입니다.");
        // }

        // // 6) 시작 시간 기록 + 상태 변경
        // od.setOrderDeliveryStartTime(LocalDateTime.now());
        // od.setOrderDeliveryStatus(DeliveryStatus.IN_DELIVERY);

        // // save() 없어도 트랜잭션 커밋 시 자동 반영(dirty checking)
    }

    @Transactional
    public void completeDeliveryByPrincipal(Long deliveryId, String principal) {
        Long delivererId = resolveDelivererIdFromPrincipal(principal);
        completeDelivery(deliveryId, delivererId);
    }

    @Transactional
    public void completeDelivery(Long deliveryId, Long delivererId) {

        OrderDelivery od = orderDeliveryRepository.findByIdForUpdate(deliveryId)
            .orElseThrow(() -> new IllegalArgumentException("배달이 없습니다."));

        if (od.getDeliverer() == null || !od.getDeliverer().getDelivererId().equals(delivererId)) {
            throw new SecurityException("본인 배달이 아닙니다.");
        }

        if (od.getOrderDeliveryStatus() != DeliveryStatus.IN_DELIVERY) {
            throw new IllegalStateException("배달완료는 배달중(IN_DELIVERY) 상태에서만 가능합니다.");
        }

        od.setOrderDeliveryCompleteTime(LocalDateTime.now());
        od.setOrderDeliveryStatus(DeliveryStatus.DELIVERED);

        StoreOrder so = od.getStoreOrder();
        if (so == null) throw new IllegalStateException("주문 정보가 없습니다.");

        if (so.getOrderStatus() != OrderStatus.DELIVERING) {
            throw new IllegalStateException("배달완료는 주문상태가 DELIVERING일 때만 가능합니다.");
        }
        so.changeStatus(OrderStatus.DELIVERED);

    }

    @Transactional(readOnly = true)
    public Long resolveDelivererIdFromPrincipal(String principal) {
        if (principal == null || !principal.contains(":")) {
            throw new IllegalArgumentException("잘못된 인증 정보입니다.");
        }

        String[] parts = principal.split(":", 2);
        String accountType = parts[0];
        String loginKey = parts[1];

        if (!"RIDER".equalsIgnoreCase(accountType)) {
            throw new IllegalArgumentException("배달원 권한이 아닙니다.");
        }

        Deliverer deliverer = delivererRepository.findByDelivererEmail(loginKey)
                .orElseThrow(() -> new IllegalArgumentException("배달원 정보를 찾을 수 없습니다."));

        return deliverer.getDelivererId();
    }

    @Transactional
    public void reopenDelivery(Long deliveryId) {

    OrderDelivery od = orderDeliveryRepository.findByIdForUpdate(deliveryId)
        .orElseThrow(() -> new IllegalArgumentException("배달 요청이 없습니다."));

    // 1. 상태 검증
    if (od.getOrderDeliveryStatus() != DeliveryStatus.CANCELLED) {
        throw new IllegalStateException("오직 취소된 요청만 다시 배정할 수 있습니다.");
    }

    // 2. 완료된 건은 복구 불가
    if (od.getOrderDeliveryCompleteTime() != null) {
        throw new IllegalStateException("배달완료된 건은 배달원 요청 불가합니다.");
    }

    // 3. 배달 컨텍스트 초기화
    od.setDeliverer(null);
    od.setOrderAcceptTime(null);
    od.setOrderDeliveryPickupTime(null);
    od.setOrderDeliveryStartTime(null);
    od.setOrderDeliveryCompleteTime(null);
    od.setOrderDeliveryActTime(null);

    // 4. 상태 복구
    od.setOrderDeliveryStatus(DeliveryStatus.PENDING);

    // 5. (선택) 재오픈 시간 기록 필드가 있다면 set
    // od.setReopenedAt(LocalDateTime.now());
    }

    /** 01/19 ~ 01/20 ******************************************************************************************************/
    /* UserDeliveryController - 배달 단건 조회 (주문 고객) */
    @Transactional(readOnly = true)
    public OrderDeliveryDTO getDeliveryForUser(Long deliveryId, Long userId) {
        OrderDelivery od = orderDeliveryRepository.findDeliveryForUser(deliveryId, userId)
                            .orElseThrow(() -> new IllegalArgumentException("404 error not your delivery_user."));
        return OrderDeliveryDTO.from(od);
    }
    
    /* UserDeliveryController - 주문들의 배달 목록 (주문 고객) */
    @Transactional(readOnly = true)
    public List<OrderDeliveryDTO> getDeliveriesByUser(Long userId) {
        List<OrderDelivery> list = orderDeliveryRepository.findDeliveriesForUser(userId);
        if (userId == null) throw new AccessDeniedException("404 error not your delivery_user.");
        return list.stream()
                .map(OrderDeliveryDTO::from)
                .toList();
    }

    /* StoreDeliveryController - 내 매장 배달 단건 조회 (매장 주인) */
    @Transactional(readOnly = true)
    public OrderDeliveryDTO getDeliveryForStore(Long deliveryId, Long storeOwnerId) {
        OrderDelivery od = orderDeliveryRepository.findForStore(deliveryId, storeOwnerId)
                        .orElseThrow(() -> new IllegalArgumentException("404 not your delivery_owner."));
        return OrderDeliveryDTO.from(od);
    }

    /* StoreDeliveryController - 내 매장 전체 배달 목록 (매장 주인) */
    @Transactional(readOnly = true)
    public List<OrderDeliveryDTO> getDeliveriesByStore(Long storeOwnerId) {
        List<OrderDelivery> list = orderDeliveryRepository.findDeliveriesByStore(storeOwnerId);
        if (storeOwnerId == null) throw new AccessDeniedException("404 not your delivery_owner.");
        return list.stream()
                .map(OrderDeliveryDTO::from)
                .toList();
    }

    /* StoreDeliveryController - 상태별 조회 (매장 주인) */
    @Transactional(readOnly = true)
    public List<OrderDeliveryDTO> getDeliveriesByStoreAndStatus(Long storeOwnerId, DeliveryStatus orderDeliveryStatus) {
        List<OrderDelivery> list = orderDeliveryRepository.findDeliveriesInStatus(storeOwnerId, orderDeliveryStatus);
        if (storeOwnerId == null) throw new AccessDeniedException("404 cannot check status_owner.");
        return list.stream()
                .map(OrderDeliveryDTO::from)
                .toList();
    }

    /* DelivererDeliveryController - 내 배달 단건 조회 (배달원) */
    @Transactional(readOnly = true)
    public OrderDeliveryDTO getDeliveryForDeliverer(Long deliveryId, Long delivererId) {
        OrderDelivery od = orderDeliveryRepository.findDeliveryForDeliverer(deliveryId, delivererId)
                        .orElseThrow(() -> new IllegalArgumentException("404 error not your delivery_rider."));
        return OrderDeliveryDTO.from(od);
    }

    /* DelivererDeliveryController - 내 배달 목록 조회 (배달원) */
    @Transactional(readOnly = true)
    public List<OrderDeliveryDTO> getDeliveriesByDeliverer(Long delivererId) {
        List<OrderDelivery> list = orderDeliveryRepository
                            .findByDeliverer_DelivererId(delivererId);
        if (delivererId == null) throw new AccessDeniedException("404 error not your delivery_rider.");
        return list.stream()
                .map(OrderDeliveryDTO::from)
                .toList();
    }

    /* DelivererDeliveryController - 상태별 내 배달 조회 (배달원) */
    @Transactional(readOnly = true)
    public List<OrderDeliveryDTO> getDeliveriesByDelivererInStatus(Long delivererId, DeliveryStatus orderDeliveryStatus) {
        List<OrderDelivery> list = orderDeliveryRepository.findDeliveriesByDelivererInStatus(delivererId, orderDeliveryStatus);
        if (delivererId == null) throw new AccessDeniedException("404 cannot check status_rider.");
        return list.stream()
                .map(OrderDeliveryDTO::from)
                .toList();
    }
    /****************************************************************************************************************/

}
