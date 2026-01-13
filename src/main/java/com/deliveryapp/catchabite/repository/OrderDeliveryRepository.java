package com.deliveryapp.catchabite.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.deliveryapp.catchabite.entity.OrderDelivery;

import jakarta.persistence.LockModeType;

public interface OrderDeliveryRepository extends JpaRepository<OrderDelivery, Long> {

    // 배달기사가 동시 수락/중복 수락을 막기위해 PESSIMISTIC_WRITE(비관적 락)를 이용
    // '비관적 락'은 트랜젝션이 끝나기 전까지는 해당 트랙젝션이 이용하고 있는 DB의 값을 변경하지 않고 놔두다가, 이용이 끝나면 변경한다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select od from OrderDelivery od where od.deliveryId = :deliveryId")
    Optional<OrderDelivery> findByIdForUpdate(@Param("deliveryId") Long deliveryId);
}
