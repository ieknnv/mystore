package org.ieknnv.mystore.controller;

import lombok.RequiredArgsConstructor;
import org.ieknnv.mystore.dto.OrderDto;
import org.ieknnv.mystore.service.OrderService;
import org.ieknnv.mystore.util.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final SecurityUtil securityUtil;

    @GetMapping("/orders")
    Mono<Rendering> getOrders(Model model) {
        return securityUtil.currentUserId()
                .flatMap(userIdOpt -> orderService.getOrders(userIdOpt.orElse((long) -1)))
                .map(orderDtos -> Rendering.view("orders")
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
