package org.ieknnv.mystore.controller;

import org.ieknnv.mystore.dto.NewItemDto;
import org.ieknnv.mystore.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/items")
@RequiredArgsConstructor
public class NewItemController {

    private final ItemService itemService;

    @GetMapping
    public String getNewItem() {
        return "new-item";
    }

    @PostMapping
    public String addItem(@ModelAttribute NewItemDto newItemDto) {
        itemService.addNewItem(newItemDto);
        return "redirect:/admin/items";
    }
}
