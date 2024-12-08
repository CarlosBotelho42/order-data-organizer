package com.br.order_data_organizer.controller;

import com.br.order_data_organizer.dto.OrderDTO;
import com.br.order_data_organizer.service.FileService;
import com.br.order_data_organizer.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final FileService fileService;

    public OrderController(OrderService orderService, FileService fileService) {
        this.orderService = orderService;
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            fileService.processDataFile(file);
            return ResponseEntity.status(HttpStatus.CREATED).body("Arquivo processado e salvo com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao processar o arquivo: " + e.getMessage());
        }
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
