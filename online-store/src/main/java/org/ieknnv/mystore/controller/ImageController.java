package org.ieknnv.mystore.controller;

import org.ieknnv.mystore.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public Mono<ResponseEntity<byte[]>> findImage(@PathVariable(name = "itemId") Long itemId) {
        return itemService.findImageByItemId(itemId)
                .map(image -> ResponseEntity
                        .ok()
                        .header("Content-Type", "image/jpeg")
                        .body(image))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
