package org.ieknnv.paymentservice.service;

import org.ieknnv.payment.model.Balance;
import org.ieknnv.payment.model.Payment;
import org.ieknnv.paymentservice.exception.NotEnoughMoney;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final Map<Long, Double> balances = new ConcurrentHashMap<>();
    private final Random random = new Random();

    @Override
    public Mono<Balance> getBalance(Long userId) {
        return Mono.just(new Balance()
                .amount(getCachedBalance(userId))
                .userId(userId));
    }

    @Override
    public Mono<Void> processPayment(Mono<Payment> paymentMono) {
        return paymentMono.flatMap(payment -> {
            var userId = payment.getUserId();
            var userBalance = BigDecimal.valueOf(getCachedBalance(userId));
            var amountToPay = BigDecimal.valueOf(payment.getAmount());
            if (userBalance.compareTo(amountToPay) < 0) {
                return Mono.error(new NotEnoughMoney("Not enough money for user " + userId));
            }
            balances.put(userId, userBalance.subtract(amountToPay).doubleValue());
            return Mono.empty();
        });
    }

    /**
     * Returns a cached balance for the given userId.
     * If it does not exist, it generates a new random balance and stores it.
     */
    private double getCachedBalance(Long userId) {
        return balances.computeIfAbsent(userId, id -> generateRandomBalance());
    }

    private double generateRandomBalance() {
        // Generates a balance between 0 and 1000 rounded to 2 decimal places
        double value = 1000 * random.nextDouble();
        return Math.round(value * 100.0) / 100.0;
    }
}
