package com.deliveryapp.catchabite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.deliveryapp.catchabite.entity.Deliverer;

public interface DelivererRepository extends JpaRepository<Deliverer, Long> {

}
