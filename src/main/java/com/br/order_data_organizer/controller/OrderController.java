package com.br.order_data_organizer.controller;

import com.br.order_data_organizer.dto.OrderDTO;
import com.br.order_data_organizer.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{orderId}")
    public OrderDTO getOrderByOrderId(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/date-range")
    public List<OrderDTO> getOrdersByDateRange(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate
    ) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return orderService.findOrdersByDateRange(start, end);
    }

    @GetMapping
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        return orderService.findAllWithPagination(pageable);
    }
}
