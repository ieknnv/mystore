package org.ieknnv.mystore.dto;

import java.util.List;

import org.ieknnv.mystore.entity.Item;
import org.springframework.data.domain.Page;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MainPageItemsDto {
    private List<List<ItemDto>> items;
    private Page<Item> page;
}
