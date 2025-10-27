package org.ieknnv.mystore.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderItemDto {
    private long id;
    private ItemDto item;
    private long count;
    private BigDecimal price;
}
