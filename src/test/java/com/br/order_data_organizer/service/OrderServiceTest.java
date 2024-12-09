package com.br.order_data_organizer.service;

import com.br.order_data_organizer.dto.OrderDTO;
import com.br.order_data_organizer.model.Order;
import com.br.order_data_organizer.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldReturnOrdersByDateRange(){
        LocalDate startDate = LocalDate.of(2024,1,1);
        LocalDate endDate = LocalDate.of(2024,1,31);
        List<Order> mockOrders = List.of(
                new Order(2L, 712L, 42L, "Carlos Botelho", 3L, new BigDecimal("1000.00"), LocalDate.of(2024, 1, 10)),
                new Order(3L, 713L, 43L, "Jessya Mendes", 5L, new BigDecimal("1000.00"), LocalDate.of(2024, 1, 15))
        );

        Mockito.when(orderRepository.findByDateRange(startDate, endDate)).thenReturn(mockOrders);

        List<OrderDTO> result = orderService.findOrdersByDateRange(startDate, endDate);

        assertEquals(2, result.size());

        OrderDTO order1 = result.getFirst();
        assertEquals(712L, order1.order_id());
        assertEquals("Carlos Botelho", order1.user().name());
        assertEquals("2024-01-10", order1.date());

        OrderDTO order2 = result.get(1);
        assertEquals(713L, order2.order_id());
        assertEquals("Jessya Mendes", order2.user().name());
        assertEquals("2024-01-15", order2.date());

        Mockito.verify(orderRepository, Mockito.times(1)).findByDateRange(startDate, endDate);
    }

    @Test
    void shouldCalculateTotalForOrders(){
        List<Order> mockOrders = List.of(
                new Order(2L, 712L, 42L, "Carlos Botelho", 3L, new BigDecimal("1000.00"), LocalDate.of(2024, 1, 10)),
                new Order(3L, 713L, 43L, "Jessya Mendes", 5L, new BigDecimal("1000.00"), LocalDate.of(2024, 1, 15))
        );

        BigDecimal total = orderService.calculateTotal(mockOrders);

        assertEquals(new BigDecimal("2000.00"), total);
    }

    @Test
    void shouldReturnOrderByIdWhenOrderExists(){
        Long orderId = 2L;
        List<Order> mockOrders = List.of(
                new Order(orderId, 712L, 42L, "Carlos Botelho", 3L, new BigDecimal("1000.00"), LocalDate.of(2024, 1, 10))
        );

        Mockito.when(orderRepository.findByOrderId(orderId)).thenReturn(mockOrders);

        OrderDTO result = orderService.getOrderById(orderId);

        assertEquals(orderId, result.order_id());
        assertEquals("1000.00", result.total());
        assertEquals("Carlos Botelho", result.user().name());

    }

}