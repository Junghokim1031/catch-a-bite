package com.deliveryapp.catchabite.service;

import java.time.Duration;
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
                .orElseThrow(() -> new IllegalArgumentException("배달 요청이 없습니다. deliveryId=" + deliveryId));

        // 2) 상태 검증(배정 가능 상태인지)
        DeliveryStatus current = orderDelivery.getOrderDeliveryStatus();

        // 이미 완료/취소는 배정 불가
        if (current == DeliveryStatus.DELIVERED || current == DeliveryStatus.CANCELLED) {
            throw new IllegalStateException("배달원에게 배달 요청이 불가합니다. status=" + current);
        }

        // 이미 배정되어 있으면 재배정 정책 결정 필요 (여기선 불가)
        if (orderDelivery.getDeliverer() != null) {
            throw new IllegalStateException("이미 배달원이 배정되어있습니다. deliveryId=" + deliveryId);
        }

        // 3) 배달원 조회
        Deliverer deliverer = delivererRepository.findById(delivererId)
                .orElseThrow(() -> new IllegalArgumentException("배달원을 찾을 수 없습니다. delivererId=" + delivererId));

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

        // 배달 건 조회 (동시성 대비 : for update)
        OrderDelivery od = orderDeliveryRepository.findByIdForUpdate(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("배달 요청이 없습니다. id=" + deliveryId));

        // 배정 여부 확인
        if (od.getDeliverer() == null) throw new IllegalStateException("배정된 배달원이 없습니다.");

        // 배정된 배달원만 수락 가능
        Long assignedDelivererId = od.getDeliverer().getDelivererId();
        if (!assignedDelivererId.equals(delivererId)) throw new IllegalStateException("배정된 배달원이 아닙니다.");

        // 상태 검증 (수락 가능한 상태인지)
        // ASSIGNED - '배차요청(수락 대기)'된 주문이 아닐 경우 이전으로 되돌림.
        if (od.getOrderDeliveryStatus() != DeliveryStatus.ASSIGNED) {
            // ACCEPTED(수락하기)는 주문이 ASSIGNED(배차요청)된 상태에서만 가능
            throw new IllegalStateException("배차요청된 주문만 수락할 수 있습니다. current=" + od.getOrderDeliveryStatus());
        }

        // 수락 시간 기록
        od.setOrderAcceptTime(LocalDateTime.now());
        // 배차 수락 상태로 변경됨
        od.setOrderDeliveryStatus(DeliveryStatus.ACCEPTED);
    }

    @Transactional
    public void pickupComplete(Long deliveryId, Long delivererId) {

        // 1. 배달 조회 (동시성 대비 락)
        OrderDelivery od = orderDeliveryRepository.findByIdForUpdate(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("배달 요청이 없습니다. id=" + deliveryId));

        // 2. 배정 여부 확인
        if (od.getDeliverer() == null) {
            throw new IllegalStateException("배달원이 배정되지 않았습니다.");
        }

        // 3. 배정된 배달원인지 확인
        if (!od.getDeliverer().getDelivererId().equals(delivererId)) {
            throw new IllegalStateException("배정된 배달원이 아닙니다.");
        }

        // 4. 상태 검증 (수락된 건만 픽업 가능)
        if (od.getOrderDeliveryStatus() != DeliveryStatus.ACCEPTED) {
            throw new IllegalStateException(
                    "배차 수락한 건에 대해서만 매장에서의 픽업이 가능합니다. current=" + od.getOrderDeliveryStatus());
        }

        // 5. 중복 픽업 방지
        if (od.getOrderDeliveryPickupTime() != null) {
            throw new IllegalStateException("이미 픽업된 배달입니다.");
        }

        // 6. 픽업 시간 기록
        od.setOrderDeliveryPickupTime(LocalDateTime.now());

        // 7. 상태 변경
        od.setOrderDeliveryStatus(DeliveryStatus.PICKED_UP);

        // save() 호출 없어도 트랜잭션 종료 시 자동 반영(dirty checking)
    }

    @Transactional
    public void startDelivery(Long deliveryId, Long delivererId) {

        // 1) 배달 조회 (동시성 대비: for update 권장)
        OrderDelivery od = orderDeliveryRepository.findByIdForUpdate(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("배달 요청이 없습니다. id=" + deliveryId));

        // 2) 배정 여부 확인
        if (od.getDeliverer() == null) {
            throw new IllegalStateException("배달원이 배정되지 않았습니다.");
        }

        // 3) 배정된 배달원인지 확인
        if (!od.getDeliverer().getDelivererId().equals(delivererId)) { // PK getter에 맞게 수정
            throw new IllegalStateException("배정된 배달원이 아닙니다.");
        }

        // 4) 상태 검증: 픽업 완료된 건만 배달 시작 가능
        if (od.getOrderDeliveryStatus() != DeliveryStatus.PICKED_UP) {
            throw new IllegalStateException(
                    "픽업한 건에 대해서만 배달 가능합니다. current=" + od.getOrderDeliveryStatus());
        }

        // 5) 중복 시작 방지
        if (od.getOrderDeliveryStartTime() != null) {
            throw new IllegalStateException("이미 시작된 배달입니다.");
        }

        // 6) 시작 시간 기록 + 상태 변경
        od.setOrderDeliveryStartTime(LocalDateTime.now());
        od.setOrderDeliveryStatus(DeliveryStatus.IN_DELIVERY);

        // save() 없어도 트랜잭션 커밋 시 자동 반영(dirty checking)
    }

    @Transactional
    public void completeDelivery(Long deliveryId, Long delivererId) {

        // 1) 배달 조회 (동시성 대비: for update)
        OrderDelivery od = orderDeliveryRepository.findByIdForUpdate(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("배달 요청이 없습니다. id=" + deliveryId));

        // 2) 배정 여부 확인
        if (od.getDeliverer() == null) {
            throw new IllegalStateException("배달원이 배정되지 않았습니다.");
        }

        // 3) 배정된 배달원인지 확인
        if (!od.getDeliverer().getDelivererId().equals(delivererId)) { // getter 이름 맞춰주세요
            throw new IllegalStateException("배정된 배달원이 아닙니다.");
        }

        // 4) 상태 검증: 배달중(IN_DELIVERY)일 때만 완료 가능
        if (od.getOrderDeliveryStatus() != DeliveryStatus.IN_DELIVERY) {
            throw new IllegalStateException(
                    "배달완료는 배달중 상태에서만 가능합니다. current=" + od.getOrderDeliveryStatus());
        }

        // 5) 중복 완료 방지
        if (od.getOrderDeliveryCompleteTime() != null) {
            throw new IllegalStateException("이미 배달완료된 주문입니다.");
        }

        // 6) 완료 시간 기록
        LocalDateTime now = LocalDateTime.now();
        od.setOrderDeliveryCompleteTime(now);

        // 7) 실제 소요 시간(분) 계산
        LocalDateTime start = od.getOrderDeliveryStartTime();
        if (start == null) {
            // startDelivery를 거치지 않고 complete가 호출된 비정상 케이스
            throw new IllegalStateException("배달 시작시간이 없습니다. 실제 소요 시간을 측정할 수 없습니다.");
        }

        long minutes = Duration.between(start, now).toMinutes();
        if (minutes < 0) minutes = 0; // 서버 시간 오차 방어 (선택)

        od.setOrderDeliveryActTime((int) minutes);

        // 8) 상태 변경
        od.setOrderDeliveryStatus(DeliveryStatus.DELIVERED);

        // save() 없어도 dirty checking으로 반영
    }

    @Transactional
    public void reopenDelivery(Long deliveryId) {

    OrderDelivery od = orderDeliveryRepository.findByIdForUpdate(deliveryId)
        .orElseThrow(() -> new IllegalArgumentException("배달 요청이 없습니다."));

    // 1. 상태 검증
    if (od.getOrderDeliveryStatus() != DeliveryStatus.CANCELLED) {
        throw new IllegalStateException("오직 취소된 요청만 다시 배정할 수 있다.");
    }

    // 2. 완료된 건은 복구 불가
    if (od.getOrderDeliveryCompleteTime() != null) {
        throw new IllegalStateException("배달완료된 건은 배달원 요청 불가.");
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


}
