package org.ieknnv.mystore.controller;

import lombok.RequiredArgsConstructor;
import org.ieknnv.mystore.dto.ActionDto;
import org.ieknnv.mystore.enums.CartAction;
import org.ieknnv.mystore.enums.SortOrder;
import org.ieknnv.mystore.service.CartService;
import org.ieknnv.mystore.service.ItemService;
import org.ieknnv.mystore.util.SecurityUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CartService cartService;
    private final SecurityUtil securityUtil;

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
        return securityUtil.currentUserId()
                .flatMap(userIdOpt -> itemService.getItems(userIdOpt.orElse(null), search, pageable))
                .map(dto -> Rendering.view("main")
                        .modelAttribute("paging", dto.getPage())
                        .modelAttribute("items", dto.getItems())
                        .modelAttribute("sort", sort)
                        .build()
                );
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/main/items/{id}")
    public Mono<Rendering> updateCartInMain(@PathVariable("id") long itemId,
            @ModelAttribute("actionDto") ActionDto actionDto) {
        return securityUtil.currentUserId()
                .flatMap(userIdOpt ->
                        cartService.updateCart(userIdOpt.orElse((long) -1), itemId, CartAction.fromValue(actionDto.getAction())))
                .thenReturn(Rendering.view("redirect:/main/items").build());
    }

    @GetMapping("/items/{id}")
    public Mono<Rendering> getItem(@PathVariable("id") long itemId) {
        return securityUtil.currentUserId()
                .flatMap(userIdOpt -> itemService.getItem(userIdOpt.orElse((long) -1), itemId))
                .map(itemDto -> Rendering
                        .view("item")
                        .modelAttribute("item", itemDto)
                        .build());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/items/{id}")
    public Mono<Rendering> updateCartInItem(@PathVariable("id") long itemId,
            @ModelAttribute("actionDto") ActionDto actionDto) {
        return securityUtil.currentUserId()
                .flatMap(userIdOpt ->
                        cartService.updateCart(userIdOpt.orElse((long) -1), itemId, CartAction.fromValue(actionDto.getAction())))
                .thenReturn(Rendering.view("redirect:/items/" + itemId).build());
    }
}
