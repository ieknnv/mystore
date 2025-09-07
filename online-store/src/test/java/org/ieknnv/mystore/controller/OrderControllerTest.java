package org.ieknnv.mystore.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;

import org.ieknnv.mystore.dto.ItemDto;
import org.ieknnv.mystore.dto.OrderDto;
import org.ieknnv.mystore.dto.OrderItemDto;
import org.ieknnv.mystore.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

@WebFluxTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderService orderService;

    @Test
    void test() {
        ItemDto mockItem = ItemDto.builder()
                .id(1L)
                .title("Mock Item")
                .description("This is a mock item for testing purposes.")
                .price(new BigDecimal("19.99"))
                .count(5L)
                .build();
        OrderItemDto mockOrderItem = OrderItemDto.builder()
                .id(100L)
                .item(mockItem)
                .count(2L)
                .price(new BigDecimal("59.98"))
                .build();
        OrderDto mockOrder = OrderDto.builder()
                .id(200L)
                .items(Collections.singletonList(mockOrderItem))
                .totalSum(new BigDecimal("59.98"))
                .build();

        when(orderService.getOrders(1L)).thenReturn(Mono.just(Collections.singletonList(mockOrder)));

        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Заказ №200"));
                    assertTrue(body.contains("Mock Item (2 шт.) 119.96 руб."));
                    assertTrue(body.contains("Сумма: 59.98 руб."));
                });
    }
}
