package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.converter.StoreOrderConverter;
import com.deliveryapp.catchabite.dto.StoreOrderDTO;
import com.deliveryapp.catchabite.entity.*;
import com.deliveryapp.catchabite.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    // =====================================================================
    // CREATE
    // =====================================================================
    @Override
    @Transactional
    public StoreOrderDTO createStoreOrder(StoreOrderDTO dto) {

        // =====================================================================
        // StoreOrder 생성에 필요한 자료가 존재하는지 확인
        // 사용자, 가게, 및 주소 
        // 이 자료들은 DB에서 확인해야 되서 따로 확인함.
        // 이외 정보는 프론트엔드에서 StoreOrderDTO 형식으로 받음.
        // =====================================================================

        AppUser appUser = appUserRepository.findById(dto.getAppUserId())
            .orElseThrow(() -> new IllegalArgumentException("StoreOrderService - appUser " + dto.getAppUserId() + "가 없음."));
        Store store = storeRepository.findById(dto.getStoreId())
            .orElseThrow(() -> new IllegalArgumentException("StoreOrderService - Store " + dto.getStoreId() + "가 없음."));
        Address address = addressRepository.findById(dto.getAddressId())
            .orElseThrow(() -> new IllegalArgumentException("StoreOrderService - Address " + dto.getAddressId() + "가 없음."));

        // =====================================================================
        // 작성한 dtoToEntity Class를 사용하여 Entity를 작성함.
        // 그 후 .save 함수를 사용하여 order에 PK를 포함함.
        // =====================================================================
        StoreOrder order = storeOrderConverter.toEntity(dto, appUser, store, address);
        StoreOrder result = storeOrderRepository.save(order);
        
        log.info("StoreOrder: orderId={}", result.getOrderId());
        return storeOrderConverter.toDto(result);
    }

    // =====================================================================
    // READ
    // =====================================================================
    @Override
    public StoreOrderDTO getStoreOrder(Long storeOrderId) {
        StoreOrder order = storeOrderRepository.findById(storeOrderId)
            .orElseThrow(() -> new IllegalArgumentException("StoreOrderService - storeOrderId " + storeOrderId + "가 없음."));
        return storeOrderConverter.toDto(order);
    }

    // =====================================================================
    // READ ALL
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
    @Override
    @Transactional
    public StoreOrderDTO updateStoreOrder(Long orderId, StoreOrderDTO dto) {
        StoreOrder order = storeOrderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("StoreOrderServiceImpl - updateStoreOrder - OrderId " + orderId + "가 존재하지 않습니다."));

        dto.getOrderStatus();
        StoreOrderDTO newOrder = storeOrderConverter.toDto(order);
        return newOrder;
    }



    // =====================================================================
    // DELETE
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

    // ===== Review에서 필요한 자료 =====

    @Override
    public StoreOrder getValidatedOrder(Long storeOrderId) {
        if (!storeOrderRepository.existsById(storeOrderId)) {
            log.error("StoreOrderService - getValidatedOrder - 주문 없음: orderId={}", 
                storeOrderId);
            throw new IllegalArgumentException("주문이 존재하지 않습니다: " + storeOrderId);
        }
        StoreOrder result = storeOrderRepository.findByOrderId(storeOrderId).orElse(null);
        if (result == null) {
            log.error("StoreOrderService - getValidatedOrder - 주문 없음: orderId={}", 
                storeOrderId);
            throw new IllegalArgumentException("주문이 존재하지 않습니다: " + storeOrderId);
        }
        
        return result;
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
