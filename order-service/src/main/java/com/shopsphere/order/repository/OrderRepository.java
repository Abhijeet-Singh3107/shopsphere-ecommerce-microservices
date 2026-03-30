package com.shopsphere.order.repository;

import com.shopsphere.order.entity.Order;
import com.shopsphere.order.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    Optional<Order> findByIdAndUserEmail(Long id, String userEmail);

    boolean existsByIdAndUserEmailAndStatusIn(Long id, String userEmail, List<OrderStatus> statuses);
}
