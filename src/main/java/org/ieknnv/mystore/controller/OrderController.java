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

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Value("${application.userId}")
    private long userId;

    @GetMapping("/orders")
    String getOrders(Model model) {
        List<OrderDto> orderDtos = orderService.getOrders(userId);
        model.addAttribute("orders", orderDtos);
        return "orders";
    }

    @GetMapping("/orders/{id}")
    String getOrder(Model model,
            @PathVariable("id") long id,
            @RequestParam(name = "newOrder", defaultValue = "false") boolean newOrder) {
        OrderDto orderDto = orderService.getOrder(id);
        model.addAttribute("order", orderDto);
        model.addAttribute("newOrder", newOrder);
        return "order";
    }
}
