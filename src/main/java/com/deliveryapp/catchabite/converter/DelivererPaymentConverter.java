package com.deliveryapp.catchabite.converter;

import org.springframework.stereotype.Component;

import com.deliveryapp.catchabite.dto.DelivererPaymentDTO;
import com.deliveryapp.catchabite.entity.Deliverer;
import com.deliveryapp.catchabite.entity.DelivererPayment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DelivererPaymentConverter {

    // dto의 Long 타입 delivererId와 entity의 Deliverer 타입 deliverer를 연결시키기 위해 사용
    // -> getReference를 이용하기 위해 필요 -> getReference는 Deliverer (entity)에서 delivererId에 접근할 수 있게하는 메서드
    @PersistenceContext
    private EntityManager em;

    public DelivererPaymentDTO toDto(DelivererPayment entity) {
        if (entity == null) return null;

        DelivererPaymentDTO delivererPaymentDTO = DelivererPaymentDTO.builder()
            .delivererPaymentId(entity.getDelivererPaymentId())
            .delivererId(entity.getDeliverer().getDelivererId())
            .delivererPaymentMinimumFee(entity.getDelivererPaymentMinimumFee())
            .delivererPaymentDistanceFee(entity.getDelivererPaymentDistanceFee())            
            .build();
        
        return delivererPaymentDTO;
    }

    public DelivererPayment toEntity(DelivererPaymentDTO dto) {
        if (dto == null) return null;

        Deliverer delivererRef = em.getReference(Deliverer.class, dto.getDelivererId());

        DelivererPayment delivererPayment = DelivererPayment.builder()
            .delivererPaymentId(dto.getDelivererPaymentId())
            .deliverer(delivererRef)
            .delivererPaymentMinimumFee(dto.getDelivererPaymentMinimumFee())
            .delivererPaymentDistanceFee(dto.getDelivererPaymentDistanceFee())
            .build();

        return delivererPayment;
    }

}
