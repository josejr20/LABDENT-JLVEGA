package com.labdent.exception;

public class PedidoNotFoundException extends RuntimeException {
    public PedidoNotFoundException(String message) {
        super(message);
    }
    
    public PedidoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}