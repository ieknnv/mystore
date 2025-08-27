package org.ieknnv.mystore.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderItemDetailDto {
    private long id;
    private long orderId;
    private long itemId;
    private long quantity;
    private BigDecimal price;
    private String title;
    private String description;
}
