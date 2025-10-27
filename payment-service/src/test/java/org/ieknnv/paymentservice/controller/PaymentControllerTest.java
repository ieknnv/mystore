package org.ieknnv.paymentservice.controller;

import org.ieknnv.payment.model.Balance;
import org.ieknnv.payment.model.Payment;
import org.ieknnv.paymentservice.service.PaymentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PaymentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PaymentService paymentService;

    @Test
    void getBalance_ShouldReturnOkAndBalance_WhenUserExists() {
        // given
        long clientId = 1L;
        // when + then
        webTestClient.get()
                .uri("/api/v1/balances/" + clientId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody(Balance.class)
                .consumeWith(response -> {
                    Balance result = response.getResponseBody();
                    Assertions.assertNotNull(result);
                    Assertions.assertEquals(clientId, result.getUserId());
                });
    }

    @Test
    void makePayment_ShouldReturnOk_WhenPaymentSucceeds() {
        // given
        Payment payment = new Payment();
        payment.setUserId(1L);
        payment.setAmount(250.50);
        // when + then
        webTestClient.post()
                .uri("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payment)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }
}
