package org.ieknnv.paymentservice.exception;

public class NotEnoughMoney extends RuntimeException {

    public NotEnoughMoney(String message) {
        super(message);
    }
}
