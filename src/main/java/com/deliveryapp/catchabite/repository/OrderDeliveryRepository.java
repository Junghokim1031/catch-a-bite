package com.deliveryapp.catchabite.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.deliveryapp.catchabite.domain.enumtype.DeliveryStatus;
import com.deliveryapp.catchabite.entity.OrderDelivery;

import jakarta.persistence.LockModeType;

public interface OrderDeliveryRepository extends JpaRepository<OrderDelivery, Long> {

    // 배달기사가 동시 수락/중복 수락을 막기위해 PESSIMISTIC_WRITE(비관적 락)를 이용
    // '비관적 락'은 트랜젝션이 끝나기 전까지는 해당 트랙젝션이 이용하고 있는 DB의 값을 변경하지 않고 놔두다가, 이용이 끝나면 변경한다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select od from OrderDelivery od where od.deliveryId = :deliveryId")
    Optional<OrderDelivery> findByIdForUpdate(@Param("deliveryId") Long deliveryId);

    /* 01/19 16:03 수정 -> 01/20  *************************************************************************************************/
    // 배달 단건 조회 (주문 고객)
    @Query("""
                select od from OrderDelivery od
                join fetch od.storeOrder so
                join fetch so.appUser au
                left join fetch od.deliverer d
                where od.deliveryId = :deliveryId
                and au.appUserId = :userId
        """)
    Optional<OrderDelivery> findDeliveryForUser(@Param("deliveryId") Long deliveryId, @Param("userId") Long userId);

    // 주문들의 배달 목록 (주문 고객)
    @Query("""
                select od from OrderDelivery od
                join fetch od.storeOrder so
                join fetch so.appUser au
                left join fetch od.deliverer d
                where au.appUserId = :userId
                order by so.orderDate desc
        """)
    List<OrderDelivery> findDeliveriesForUser(@Param("userId") Long userId);

    // 내 매장 배달 단건 조회 (매장 주인)
    @Query("""
                select od from OrderDelivery od
                join fetch od.storeOrder so
                join fetch so.store s
                join fetch s.storeOwner ow
                where od.deliveryId = :deliveryId
                and ow.storeOwnerId = :storeOwnerId
        """)
    Optional<OrderDelivery> findForStore(@Param("deliveryId") Long deliveryId, @Param("storeOwnerId") Long storeOwnerId);

    // 내 매장 전체 배달 목록 조회 (매장 주인)
    @Query("""
                select distinct od from OrderDelivery od
                join fetch od.storeOrder so
                join fetch so.store s
                join fetch s.storeOwner ow
                where ow.storeOwnerId = :storeOwnerId
        """)
    List<OrderDelivery> findDeliveriesByStore(@Param("storeOwnerId") Long storeOwnerId);

    // 상태별 조회 (매장 주인)
    @Query("""
                select distinct od from OrderDelivery od
                join fetch od.storeOrder so   
                join fetch so.store s 
                where s.storeOwner.storeOwnerId = :storeOwnerId
                and od.orderDeliveryStatus = :orderDeliveryStatus             
        """)
    List<OrderDelivery> findDeliveriesInStatus(
        @Param("storeOwnerId") Long storeOwnerId, 
        @Param("orderDeliveryStatus") DeliveryStatus orderDeliveryStatus
        );

    // 내 배달 단건 조회 (배달원), '배정전' 상태에서는 delivererId가 null이 될 수 있기에 외부 조인을 함
    @Query("""
                select od from OrderDelivery od
                join fetch od.storeOrder so
                left join fetch od.deliverer d
                where od.deliveryId = :deliveryId
                and d.delivererId = :delivererId
        """)
     Optional<OrderDelivery> findDeliveryForDeliverer(
        @Param("deliveryId") Long deliveryId,
        @Param("delivererId") Long delivererId
        );

    // 내 배달 목록 (배달원)
    // order_delivery와 deliverer 테이블을 내부 조인시켜 delivererId를 찾는다.
    @Query("""
                select distinct od from OrderDelivery od
                join fetch od.storeOrder so
                join fetch od.deliverer d
                where d.delivererId = :delivererId
                order by od.orderDeliveryCreatedDate desc
        """)
     List<OrderDelivery> findByDeliverer_DelivererId(@Param("delivererId") Long delivererId);

      // 상태별 내 배달 조회 (배달원)
     @Query("""
                select distinct od from OrderDelivery od
                join fetch od.storeOrder so
                join fetch od.deliverer d
                where d.delivererId = :delivererId
                and od.orderDeliveryStatus = :orderDeliveryStatus
                order by od.orderDeliveryCreatedDate desc
        """)
     List<OrderDelivery> findDeliveriesByDelivererInStatus(
        @Param("delivererId") Long delivererId, 
        @Param("orderDeliveryStatus") DeliveryStatus orderDeliveryStatus
        );
    /*******************************************************************************************************************/

    /* 01/21 - 배달원 정산 시 기간 조회에 필드명 반영 ***********************************************************************/
    List<OrderDelivery> findByDeliverer_DelivererIdAndOrderDeliveryStatusAndOrderDeliveryCompleteTimeBetween(
        Long delivererId,
        DeliveryStatus orderDeliveryStatus,
        LocalDateTime from,
        LocalDateTime to
    );

    List<OrderDelivery> findByDeliveryIdInAndDeliverer_DelivererId(List<Long> deliveryIds, Long delivererId);
    /*******************************************************************************************************************/
}
