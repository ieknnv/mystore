package org.ieknnv.mystore.controller;

import lombok.RequiredArgsConstructor;
import org.ieknnv.mystore.dto.ActionDto;
import org.ieknnv.mystore.dto.CartPageDto;
import org.ieknnv.mystore.enums.CartAction;
import org.ieknnv.mystore.service.CartService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Value("${application.userId}")
    private long userId;

    @GetMapping("/cart/items")
    public Mono<Rendering> getCart(Model model) {
        Mono<CartPageDto> cartPageDto = cartService.getCartForUser(userId);
        return Mono.just(Rendering
                .view("cart")
                .modelAttribute("items", cartPageDto.mapNotNull(CartPageDto::getItemDtoList))
                .modelAttribute("total", cartPageDto.mapNotNull(CartPageDto::getTotal))
                .modelAttribute("empty", cartPageDto.mapNotNull(CartPageDto::isCartEmpty))
                .modelAttribute("userBalance", cartPageDto.mapNotNull(CartPageDto::getUserBalance))
                .modelAttribute("paymentError", cartPageDto.mapNotNull(CartPageDto::getPaymentError))
                .modelAttribute("enablePayment", cartPageDto.mapNotNull(CartPageDto::isEnablePayment))
                .modelAttribute("userBalanceAvailable", cartPageDto.mapNotNull(CartPageDto::isUserBalanceAvailable))
                .build());
    }

    @PostMapping("cart/items/{id}")
    public Mono<Rendering> updateCart(@PathVariable("id") long itemId,
                                      @ModelAttribute("actionDto") ActionDto actionDto) {
        return cartService.updateCart(userId, itemId, CartAction.fromValue(actionDto.getAction()))
                .thenReturn(Rendering.view("redirect:/cart/items").build());
    }

    @PostMapping("/buy")
    public Mono<Rendering> buyCart() {
        return cartService
                .buyCart(userId)
                .map(order -> Rendering.view("redirect:/orders/" + order.getId() + "?newOrder=true").build());
    }
}
