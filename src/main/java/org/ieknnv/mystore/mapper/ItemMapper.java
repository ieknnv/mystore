package org.ieknnv.mystore.mapper;

import java.io.IOException;

import org.ieknnv.mystore.dto.NewItemDto;
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
}
