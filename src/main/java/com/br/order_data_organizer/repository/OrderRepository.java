package com.br.order_data_organizer.repository;

import com.br.order_data_organizer.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{

    @Query(value = """
    SELECT * FROM orders
    WHERE date BETWEEN :startDate AND :endDate
    """, nativeQuery = true)
    List<Order> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<Order> findByOrderId(Long orderId);
}
