package org.ieknnv.paymentservice.controller;

import org.ieknnv.payment.model.ErrorResponse;
import org.ieknnv.paymentservice.exception.NotEnoughMoney;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotEnoughMoney.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNotEnoughMoney(NotEnoughMoney ex) {
        var errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setTimestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
        return Mono.just(ResponseEntity.status(HttpStatusCode.valueOf(422)).body(errorResponse));
    }
}
