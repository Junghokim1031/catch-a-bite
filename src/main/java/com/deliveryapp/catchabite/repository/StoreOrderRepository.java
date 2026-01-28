package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.domain.enumtype.OrderStatus;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.entity.StoreOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StoreOrderRepository extends JpaRepository<StoreOrder, Long> {

    Optional<StoreOrder> findByOrderId(Long orderId);

    Optional<StoreOrder> findByOrderIdAndStore_StoreId(Long orderId, Long storeId);

    // 페이징 기본(상태/기간 없음)
    Page<StoreOrder> findByStore_StoreId(Long storeId, Pageable pageable);

    // 페이징 + 상태
    Page<StoreOrder> findByStore_StoreIdAndOrderStatus(Long storeId, OrderStatus orderStatus, Pageable pageable);

    // 페이징 + 기간
    Page<StoreOrder> findByStore_StoreIdAndOrderDateBetween(Long storeId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    // 페이징 + 상태 + 기간
    Page<StoreOrder> findByStore_StoreIdAndOrderStatusAndOrderDateBetween(
            Long storeId,
            OrderStatus orderStatus,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

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

    /**
     * 주문과 주문 상품들을 한 번에 조회
     * 주문 내역 리스트를 보여줄 때 N+1 문제를 방지합니다.
     */
    @Query("SELECT DISTINCT o FROM StoreOrder o " +
           "LEFT JOIN FETCH o.orderItems " +
           "WHERE o.appUser.appUserId = :userId " +
           "ORDER BY o.orderDate DESC")
    List<StoreOrder> findAllWithItemsByUserId(@Param("userId") Long userId);

    /**
     * 특정 사용자의 주문 내역을 바탕으로 방문 횟수가 많은 가게를 내림차순으로 조회합니다.
     * GROUP BY와 COUNT를 사용하여 DB 레벨에서 정렬을 수행합니다.
     */
    @Query("SELECT o.store FROM StoreOrder o " +
           "WHERE o.appUser.appUserId = :userId " +
           "GROUP BY o.store " +
           "ORDER BY COUNT(o) DESC")
    List<Store> findMostFrequentStores(@Param("userId") Long userId, Pageable pageable);
}
