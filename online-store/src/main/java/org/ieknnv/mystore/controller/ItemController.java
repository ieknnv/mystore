package org.ieknnv.mystore.controller;

import org.ieknnv.mystore.dto.ActionDto;
import org.ieknnv.mystore.enums.CartAction;
import org.ieknnv.mystore.enums.SortOrder;
import org.ieknnv.mystore.service.CartService;
import org.ieknnv.mystore.service.ItemService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CartService cartService;

    @Value("${application.userId}")
    private long userId;

    @GetMapping("/")
    public Mono<Rendering> redirectToAllItems() {
        return Mono.just(Rendering.view("redirect:main/items").build());
    }

    @GetMapping("/main/items")
    public Mono<Rendering> getAllItems(@RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "sort", defaultValue = "NO") SortOrder sort,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort.getSort());
        return itemService.getItems(userId, search, pageable)
                .map(dto -> Rendering.view("main")
                        .modelAttribute("paging", dto.getPage())
                        .modelAttribute("items", dto.getItems())
                        .modelAttribute("sort", sort)
                        .build()
                );
    }

    @PostMapping("/main/items/{id}")
    public Mono<Rendering> updateCartInMain(@PathVariable("id") long itemId,
            @ModelAttribute("actionDto") ActionDto actionDto) {
        return cartService.updateCart(userId, itemId, CartAction.fromValue(actionDto.getAction()))
                .thenReturn(Rendering.view("redirect:/main/items").build());
    }

    @GetMapping("/items/{id}")
    public Mono<Rendering> getItem(@PathVariable("id") long itemId) {
        var item = itemService.getItem(userId, itemId);
        return Mono.just(Rendering
                .view("item")
                .modelAttribute("item", item)
                .build());
    }

    @PostMapping("/items/{id}")
    public Mono<Rendering> updateCartInItem(@PathVariable("id") long itemId,
            @ModelAttribute("actionDto") ActionDto actionDto) {
        return cartService.updateCart(userId, itemId, CartAction.fromValue(actionDto.getAction()))
                .thenReturn(Rendering.view("redirect:/items/" + itemId).build());
    }
}
