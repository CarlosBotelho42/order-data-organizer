package com.br.order_data_organizer.service;

import com.br.order_data_organizer.model.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    private final OrderService orderService;

    public FileService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void processDataFile(MultipartFile file) {
        try (BufferedReader reader = createBufferedReader(file)) {
            List<Order> orders = normalizeOrderLines(reader);
            orderService.saveOrders(orders);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar o arquivo... =(", e);
        }
    }

    private BufferedReader createBufferedReader(MultipartFile file) throws IOException, IOException {
        return new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
    }

    private List<Order> normalizeOrderLines (BufferedReader reader) throws IOException {
            List<Order> orders = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Order order = setOrderLines(line);
                orders.add(order);
            }

            return orders;
    }

    private Order setOrderLines(String line) {
        String userId = line.substring(0, 10).trim();
        String userName = line.substring(10, 55).trim();
        String orderId = line.substring(55, 65).trim();
        String productId = line.substring(65, 75).trim();
        BigDecimal productValue = new BigDecimal(line.substring(75, 87).trim());
        LocalDate purchaseDate = LocalDate.parse(line.substring(87, 95).trim(), DateTimeFormatter.ofPattern("yyyyMMdd"));

        Order order = new Order();
        order.setOrderId(Long.parseLong(orderId));
        order.setUserId(Long.parseLong(userId));
        order.setUserName(userName);
        order.setProductId(Long.parseLong(productId));
        order.setProductValue(productValue);
        order.setDate(purchaseDate);

        return order;
    }
}
