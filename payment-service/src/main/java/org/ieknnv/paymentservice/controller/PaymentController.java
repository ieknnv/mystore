package org.ieknnv.paymentservice.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ieknnv.payment.api.BalancesApi;
import org.ieknnv.payment.api.PaymentsApi;
import org.ieknnv.payment.model.Balance;
import org.ieknnv.payment.model.Payment;
import org.ieknnv.paymentservice.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentController implements BalancesApi, PaymentsApi {

    private final PaymentService paymentService;

    @Override
    public Mono<ResponseEntity<Balance>> getBalance(
            @Parameter(name = "userId", description = "user ID", required = true, in = ParameterIn.PATH)
            @PathVariable("userId") Long userId, @Parameter(hidden = true) final ServerWebExchange exchange) {
        return paymentService.getBalance(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> makePayment(
            @Parameter(name = "Payment", description = "details of payment", required = true) @Valid @RequestBody Mono<Payment> payment,
            @Parameter(hidden = true) final ServerWebExchange exchange) {
        return paymentService.processPayment(payment)
                .then(Mono.just(ResponseEntity.ok().build()));
    }


}
