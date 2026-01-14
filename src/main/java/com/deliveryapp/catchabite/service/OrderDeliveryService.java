package com.deliveryapp.catchabite.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.deliveryapp.catchabite.domain.enumtype.DeliveryStatus;
import com.deliveryapp.catchabite.entity.Deliverer;
import com.deliveryapp.catchabite.entity.OrderDelivery;
import com.deliveryapp.catchabite.repository.DelivererRepository;
import com.deliveryapp.catchabite.repository.OrderDeliveryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderDeliveryService {

    private final OrderDeliveryRepository orderDeliveryRepository;
    private final DelivererRepository delivererRepository;

    @Transactional
    public void assignDeliverer(Long deliveryId, Long delivererId) {

        // 1) 배달 조회
        OrderDelivery orderDelivery = orderDeliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("OrderDelivery not found. deliveryId=" + deliveryId));

        // 2) 상태 검증(배정 가능 상태인지)
        DeliveryStatus current = orderDelivery.getOrderDeliveryStatus();

        // 이미 완료/취소는 배정 불가
        if (current == DeliveryStatus.DELIVERED || current == DeliveryStatus.CANCELLED) {
            throw new IllegalStateException("Cannot assign deliverer. status=" + current);
        }

        // 이미 배정되어 있으면 재배정 정책 결정 필요 (여기선 불가)
        if (orderDelivery.getDeliverer() != null) {
            throw new IllegalStateException("Deliverer already assigned. deliveryId=" + deliveryId);
        }

        // 3) 배달원 조회
        Deliverer deliverer = delivererRepository.findById(delivererId)
                .orElseThrow(() -> new IllegalArgumentException("Deliverer not found. delivererId=" + delivererId));

        // 4) 배달원 상태 검증(예: 운행 가능 여부)
        // deliverer_status가 YesNo.Y/N 같은 구조라면:
        // if (deliverer.getStatus() != YesNo.Y) throw new IllegalStateException("Deliverer not available.");

        // 5) 배정 + 상태 변경
        orderDelivery.setDeliverer(deliverer);
        orderDelivery.setOrderDeliveryStatus(DeliveryStatus.ASSIGNED);

        // (선택) 배정 시간 필드가 따로 있다면 여기서 set
        // orderDelivery.setAssignedAt(LocalDateTime.now());

        // 6) save는 필수는 아님(dirty checking) but 명시해도 OK
        // orderDeliveryRepository.save(orderDelivery);
    }

    @Transactional
    public void accept(Long deliveryId, Long delivererId) {

    OrderDelivery od = orderDeliveryRepository.findByIdForUpdate(deliveryId)
            .orElseThrow(() -> new IllegalArgumentException("OrderDelivery not found. id=" + deliveryId));

    if (od.getDeliverer() == null) throw new IllegalStateException("No assigned deliverer.");

    Long assignedDelivererId = od.getDeliverer().getDelivererId();
    if (!assignedDelivererId.equals(delivererId)) throw new IllegalStateException("Not assigned deliverer.");

    if (od.getOrderDeliveryStatus() != DeliveryStatus.ASSIGNED) {
        throw new IllegalStateException("Must be ASSIGNED to accept. current=" + od.getOrderDeliveryStatus());
    }

    od.setOrderAcceptTime(LocalDateTime.now());
    od.setOrderDeliveryStatus(DeliveryStatus.ACCEPTED);
    }

}
