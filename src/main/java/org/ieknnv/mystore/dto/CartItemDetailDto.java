package org.ieknnv.mystore.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CartItemDetailDto {
    private long id;
    private long cartId;
    private long itemId;
    private String title;
    private String description;
    private BigDecimal price;
    private long quantity;
}
