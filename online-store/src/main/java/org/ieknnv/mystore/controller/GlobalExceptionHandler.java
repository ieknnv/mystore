package org.ieknnv.mystore.controller;

import org.ieknnv.mystore.exception.PaymentException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentException.class)
    public Mono<Rendering> handlePaymentException(PaymentException ex) {
        return Mono.just(Rendering
                .view("error")
                .modelAttribute("error", ex.getMessage())
                .build());
    }
}
