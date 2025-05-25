package org.ieknnv.mystore.controller;

import org.ieknnv.mystore.dto.CartPageDto;
import org.ieknnv.mystore.enums.CartAction;
import org.ieknnv.mystore.service.CartService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Value("${application.userId}")
    private long userId;

    @GetMapping("/cart/items")
    public String getCart(Model model) {
        CartPageDto cartPageDto = cartService.getCartForUser(userId);
        model.addAttribute("items", cartPageDto.getItemDtoList());
        model.addAttribute("total", cartPageDto.getTotal());
        model.addAttribute("empty", cartPageDto.isCartEmpty());
        return "cart";
    }

    @PostMapping("cart/items/{id}")
    public String updateCart(Model model,
            @PathVariable("id") long itemId,
            @RequestParam("action") String action) {
        cartService.updateCart(userId, itemId, CartAction.fromValue(action));
        return "redirect:/cart/items";
    }

    @PostMapping("/buy")
    public String buyCart() {
        long orderId = cartService.buyCart(userId);
        return "redirect:/orders/" + orderId + "?newOrder=true";
    }
}
