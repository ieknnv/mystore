package org.ieknnv.mystore.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.ieknnv.mystore.dto.CartItemDetailDto;
import org.ieknnv.mystore.dto.ItemDto;
import org.ieknnv.mystore.dto.NewItemDto;
import org.ieknnv.mystore.entity.Item;

public final class ItemMapper {

    private ItemMapper() {
    }

    public static Item toEntity(NewItemDto dto, byte[] imageBytes) {
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .itemImage(imageBytes)
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

    public static ItemDto toDto(CartItemDetailDto cartItemDetail) {
        return ItemDto.builder()
                .id(cartItemDetail.getItemId())
                .title(cartItemDetail.getTitle())
                .description(cartItemDetail.getDescription())
                .price(cartItemDetail.getPrice())
                .count(cartItemDetail.getQuantity())
                .build();
    }

    public static List<ItemDto> toDto(List<CartItemDetailDto> cartItemDetails) {
        return cartItemDetails.stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}
