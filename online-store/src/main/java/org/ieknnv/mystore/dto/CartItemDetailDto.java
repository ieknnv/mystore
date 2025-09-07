package org.ieknnv.mystore.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDetailDto {
    private long id;
    private long cartId;
    private long itemId;
    private String title;
    private String description;
    private BigDecimal price;
    private long quantity;
}
