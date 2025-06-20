package org.ieknnv.mystore.controller;

import java.util.List;

import org.ieknnv.mystore.dto.OrderDto;
import org.ieknnv.mystore.service.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Value("${application.userId}")
    private long userId;

    @GetMapping("/orders")
    Mono<Rendering> getOrders(Model model) {
        Mono<List<OrderDto>> orderDtos = orderService.getOrders(userId);
        return Mono.just(Rendering.view("orders")
                .modelAttribute("orders", orderDtos)
                .build());
    }

    @GetMapping("/orders/{id}")
    Mono<Rendering> getOrder(@PathVariable("id") long id,
            @RequestParam(name = "newOrder", defaultValue = "false") boolean newOrder) {
        Mono<OrderDto> orderDto = orderService.getOrder(id);
        return Mono.just(Rendering.view("order")
                .modelAttribute("order", orderDto)
                .modelAttribute("newOrder", newOrder)
                .build());
    }
}
