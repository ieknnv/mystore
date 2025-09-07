package org.ieknnv.mystore.controller;

import org.ieknnv.mystore.dto.NewItemDto;
import org.ieknnv.mystore.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/admin/items")
@RequiredArgsConstructor
public class NewItemController {

    private final ItemService itemService;

    @GetMapping
    public Mono<Rendering> getNewItem() {
        return Mono.just(Rendering.view("new-item").build());
    }

    @PostMapping(consumes = "multipart/form-data")
    public Mono<Rendering> addItem(@ModelAttribute("newItemDto") NewItemDto newItemDto) {
        return itemService.addNewItem(newItemDto)
                .thenReturn(Rendering.view("redirect:/admin/items").build());
    }
}
