package org.ieknnv.mystore.exception;

public class ItemProcessingException extends RuntimeException {

    public ItemProcessingException(String message) {
        super(message);
    }

    public ItemProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
