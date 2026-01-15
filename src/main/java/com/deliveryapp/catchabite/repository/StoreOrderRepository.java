package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.domain.enumtype.OrderStatus;
import com.deliveryapp.catchabite.entity.StoreOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreOrderRepository extends JpaRepository<StoreOrder, Long> {

    Optional<StoreOrder> findByOrderIdAndStore_StoreId(Long orderId, Long storeId);

    List<StoreOrder> findAllByStore_StoreIdOrderByOrderDateDesc(Long storeId);

    List<StoreOrder> findAllByStore_StoreIdAndOrderStatusOrderByOrderDateDesc(Long storeId, OrderStatus orderStatus);

    // 주문 상세(헤더 + items + options) 한 번에 가져오기
    @Query("""
        select distinct o
        from StoreOrder o
        left join fetch o.orderItems oi
        left join fetch oi.orderOptions oo
        where o.orderId = :orderId
          and o.store.storeId = :storeId
    """)
    Optional<StoreOrder> findDetailByOrderIdAndStoreId(
            @Param("orderId") Long orderId,
            @Param("storeId") Long storeId
    );
}
