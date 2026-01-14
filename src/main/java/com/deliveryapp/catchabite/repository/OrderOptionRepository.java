package com.deliveryapp.catchabite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.deliveryapp.catchabite.entity.OrderOption;

public interface OrderOptionRepository extends JpaRepository<OrderOption, Long> {

}
