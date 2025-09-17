package org.ieknnv.paymentservice.service;

import org.ieknnv.payment.model.Balance;
import org.ieknnv.payment.model.Payment;
import reactor.core.publisher.Mono;

public interface PaymentService {

    Mono<Balance> getBalance(Long userId);

    Mono<Void> processPayment(Mono<Payment> paymentMono);
}
