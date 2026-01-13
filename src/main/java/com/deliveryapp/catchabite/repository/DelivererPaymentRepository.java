package com.deliveryapp.catchabite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.deliveryapp.catchabite.entity.DelivererPayment;

public interface DelivererPaymentRepository extends JpaRepository<DelivererPayment, Long> {

}
