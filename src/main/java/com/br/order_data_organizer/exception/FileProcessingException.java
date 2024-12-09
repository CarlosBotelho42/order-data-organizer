package com.br.order_data_organizer.exception;

public class FileProcessingException extends RuntimeException {
    public FileProcessingException(String message, Throwable cause){
        super(message, cause);
    }
}
