package com.br.order_data_organizer.service;

import com.br.order_data_organizer.dto.OrderDTO;
import com.br.order_data_organizer.dto.ProductDTO;
import com.br.order_data_organizer.dto.UserDTO;
import com.br.order_data_organizer.model.Order;
import com.br.order_data_organizer.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Page<OrderDTO> findAllWithPagination(Pageable pageable) {
        Page<Order> pagedOrders = orderRepository.findAll(pageable);

        return pagedOrders.map(order -> buildOrderDTO(order.getOrderId(), List.of(order)));
    }

    public OrderDTO getOrderById(Long orderId) {
        List<Order> orders = orderRepository.findByOrderId(orderId);
        return buildOrderDTO(orderId, orders);
    }

    public List<OrderDTO> findOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Order> orders = orderRepository.findByDateRange(startDate, endDate);

        if (orders.isEmpty()) {
            throw new RuntimeException("Nenhum pedido encontrado no intervalo especificado.");
        }

        return orders.stream()
                .collect(Collectors.groupingBy(Order::getOrderId)) // Agrupa pelo orderId
                .entrySet()
                .stream()
                .map(entry -> buildOrderDTO(entry.getKey(), entry.getValue())) // Usa o método auxiliar
                .toList();
    }


    private OrderDTO buildOrderDTO(Long orderId, List<Order> orders) {
        if (orders.isEmpty()) {
            throw new RuntimeException("Nenhum pedido encontrado.");
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

    private BigDecimal calculateTotal(List<Order> orders) {
        return orders.stream()
                .map(Order::getProductValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<ProductDTO> createProductList(List<Order> orders) {
        return orders.stream()
                .map(order -> new ProductDTO(order.getProductId(), order.getProductValue().toString()))
                .toList();
    }


    //TODO Separar em uma classe exclusva para processamento de arquvos
    //TODO Separar em varios metodos
    public void processAndSaveFile(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            List<Order> orders = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // Processa cada linha do arquivo
                String userId = line.substring(0, 10).trim();
                String userName = line.substring(10, 55).trim();
                String orderId = line.substring(55, 65).trim();
                String productId = line.substring(65, 75).trim();
                BigDecimal productValue = new BigDecimal(line.substring(75, 87).trim());
                LocalDate purchaseDate = LocalDate.parse(line.substring(87, 95).trim(), DateTimeFormatter.ofPattern("yyyyMMdd"));

                // Cria um objeto Order com os dados extraidos
                Order order = new Order();
                order.setOrderId(Long.parseLong(orderId));
                order.setUserId(Long.parseLong(userId));
                order.setUserName(userName);
                order.setProductId(Long.parseLong(productId));
                order.setProductValue(productValue);
                order.setDate(purchaseDate);
                orders.add(order);
            }

            orderRepository.saveAll(orders);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar o arquivo... =(", e);
        }
    }
}
