package com.br.order_data_organizer.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private Long userId;

    private String userName;

    private Long productId;

    private BigDecimal productValue;

    @Column(name = "order_date")
    private LocalDate date;

    public Order(Long id, Long orderId, Long userId, String userName, Long productId, BigDecimal productValue, LocalDate date) {
        this.id = id;
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.productId = productId;
        this.productValue = productValue;
        this.date = date;
    }

    public Order() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getProductValue() {
        return productValue;
    }

    public void setProductValue(BigDecimal productValue) {
        this.productValue = productValue;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
