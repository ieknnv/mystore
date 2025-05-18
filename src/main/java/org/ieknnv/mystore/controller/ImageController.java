package org.ieknnv.mystore.controller;

import org.ieknnv.mystore.service.ItemService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ResponseEntity<byte[]> findImage(@PathVariable(name = "itemId") Long itemId) {
        byte[] imageBytes = itemService.findImageByItemId(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
}
