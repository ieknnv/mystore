package org.ieknnv.mystore.controller;

import java.math.BigDecimal;

import org.ieknnv.mystore.dto.NewItemDto;
import org.ieknnv.mystore.service.ItemService;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
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
    public Mono<Rendering> addItem(@RequestPart("name") String name,
            @RequestPart("description") String description,
            @RequestPart("price") BigDecimal price,
            @RequestPart("image") FilePart image) {
        NewItemDto newItemDto = NewItemDto.builder()
                .name(name)
                .description(description)
                .price(price)
                .image(image)
                .build();
        return itemService.addNewItem(newItemDto)
                .thenReturn(Rendering.view("redirect:/admin/items").build());
    }
}
