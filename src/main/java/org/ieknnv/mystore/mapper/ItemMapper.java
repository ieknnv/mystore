package org.ieknnv.mystore.mapper;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.ieknnv.mystore.dto.ItemDto;
import org.ieknnv.mystore.dto.NewItemDto;
import org.ieknnv.mystore.entity.CartItem;
import org.ieknnv.mystore.entity.Item;

public final class ItemMapper {

    private ItemMapper() {
    }

    public static Item toEntity(NewItemDto dto) throws IOException {
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .itemImage(dto.getImage() == null ? null : dto.getImage().getBytes())
                .price(dto.getPrice()).
                build();
    }

    public static ItemDto toDto(Item entity, long quantity) {
        return ItemDto.builder()
                .id(entity.getId())
                .title(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .count(quantity)
                .build();
    }

    public static List<ItemDto> toDto(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(ci -> toDto(ci.getItem(), ci.getQuantity()))
                .collect(Collectors.toList());
    }
}
