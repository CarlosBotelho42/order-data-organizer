package com.br.order_data_organizer.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message){
        super(message);
    }
}
