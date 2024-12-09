package com.br.order_data_organizer.service;

import com.br.order_data_organizer.dto.OrderDTO;
import com.br.order_data_organizer.dto.ProductDTO;
import com.br.order_data_organizer.dto.UserDTO;
import com.br.order_data_organizer.exception.OrderNotFoundException;
import com.br.order_data_organizer.model.Order;
import com.br.order_data_organizer.repository.OrderRepository;
import com.br.order_data_organizer.util.ErrorMessages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void saveOrders(List<Order> orders) {
        orderRepository.saveAll(orders);
    }

    public Page<OrderDTO> findAllWithPagination(Pageable pageable) {
        Page<Order> pagedOrders = orderRepository.findAll(pageable);
        return pagedOrders.map(order -> buildOrderDTO(order.getOrderId(), List.of(order)));
    }

    public OrderDTO getOrderById(Long orderId) {
        List<Order> orders = orderRepository.findByOrderId(orderId);
        if (orders.isEmpty()) {
            throw new OrderNotFoundException(ErrorMessages.ORDER_NOT_FOUND);
        }
        return buildOrderDTO(orderId, orders);
    }


    public List<OrderDTO> findOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Order> orders = orderRepository.findByDateRange(startDate, endDate);

        if (orders.isEmpty()) {
            throw new RuntimeException(ErrorMessages.DATE_RANGE_NOT_FOUND);
        }

        return orders.stream()
                .collect(Collectors.groupingBy(Order::getOrderId))
                .entrySet()
                .stream()
                .map(entry -> buildOrderDTO(entry.getKey(), entry.getValue()))
                .toList();
    }


    private OrderDTO buildOrderDTO(Long orderId, List<Order> orders) {
        if (orders.isEmpty()) {
            throw new OrderNotFoundException(ErrorMessages.UNAVAILABLE_ORDERS);
        }

        Order firstOrder = orders.getFirst();
        UserDTO user = createUserDTO(firstOrder);
        BigDecimal total = calculateTotal(orders);
        List<ProductDTO> products = createProductList(orders);

        return new OrderDTO(
                user,
                orderId,
                total.toString(),
                firstOrder.getDate().toString(),
                products
        );
    }

    private UserDTO createUserDTO(Order order) {
        return new UserDTO(order.getUserId(), order.getUserName());
    }

    public BigDecimal calculateTotal(List<Order> orders) {
        return orders.stream()
                .map(Order::getProductValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<ProductDTO> createProductList(List<Order> orders) {
        return orders.stream()
                .map(order -> new ProductDTO(order.getProductId(), order.getProductValue().toString()))
                .toList();
    }
}
