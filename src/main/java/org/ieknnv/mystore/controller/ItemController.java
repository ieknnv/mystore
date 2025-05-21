package org.ieknnv.mystore.controller;

import org.ieknnv.mystore.dto.MainPageItemsDto;
import org.ieknnv.mystore.enums.CartAction;
import org.ieknnv.mystore.enums.SortOrder;
import org.ieknnv.mystore.service.CartService;
import org.ieknnv.mystore.service.ItemService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CartService cartService;

    @Value("${application.userId}")
    private long userId;

    @GetMapping("/")
    public String redirectToAllItems(Model model) {
        return "redirect:main/items";
    }

    @GetMapping("/main/items")
    public String getAllItems(Model model,
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "sort", defaultValue = "NO") SortOrder sort,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort.getSort());
        MainPageItemsDto dto = itemService.getItems(userId, search, pageable);
        model.addAttribute("paging", dto.getPage());
        model.addAttribute("items", dto.getItems());
        model.addAttribute("sort", sort);
        return "main";
    }

    @PostMapping("main/items/{id}")
    public String updateCartInMain(Model model,
            @PathVariable("id") long itemId,
            @RequestParam("action") String action) {
        cartService.updateCart(userId, itemId, CartAction.fromValue(action));
        return "redirect:/main/items";
    }
}
