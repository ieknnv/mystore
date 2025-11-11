package org.ieknnv.mystore.controller;

import lombok.RequiredArgsConstructor;
import org.ieknnv.mystore.dto.ActionDto;
import org.ieknnv.mystore.enums.CartAction;
import org.ieknnv.mystore.service.CartService;
import org.ieknnv.mystore.util.SecurityUtil;
import org.springframework.stereotype.Controller;
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
    private final SecurityUtil securityUtil;

    @GetMapping("/cart/items")
    public Mono<Rendering> getCart() {
        return securityUtil.currentUserId()
                .flatMap(userIdOpt -> cartService.getCartForUser(userIdOpt.orElse((long) -1)))
                .map(cartPageDto -> Rendering
                        .view("cart")
                        .modelAttribute("items", cartPageDto.getItemDtoList())
                        .modelAttribute("total", cartPageDto.getTotal())
                        .modelAttribute("empty", cartPageDto.isCartEmpty())
                        .modelAttribute("userBalance", cartPageDto.getUserBalance())
                        .modelAttribute("paymentError", cartPageDto.getPaymentError())
                        .modelAttribute("enablePayment", cartPageDto.isEnablePayment())
                        .modelAttribute("userBalanceAvailable", cartPageDto.isUserBalanceAvailable())
                        .build());
    }

    @PostMapping("cart/items/{id}")
    public Mono<Rendering> updateCart(@PathVariable("id") long itemId,
                                      @ModelAttribute("actionDto") ActionDto actionDto) {
        return securityUtil.currentUserId()
                .flatMap(userIdOpt ->
                        cartService.updateCart(userIdOpt.orElse((long) -1), itemId, CartAction.fromValue(actionDto.getAction())))
                .thenReturn(Rendering.view("redirect:/cart/items").build());
    }

    @PostMapping("/buy")
    public Mono<Rendering> buyCart() {
        return securityUtil.currentUserId()
                .flatMap(userIdOpt -> cartService.buyCart(userIdOpt.orElse((long) -1)))
                .map(order -> Rendering.view("redirect:/orders/" + order.getId() + "?newOrder=true").build());
    }
}
