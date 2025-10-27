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
public class OrderItemDetailDto {
    private long id;
    private long orderId;
    private long itemId;
    private long quantity;
    private BigDecimal price;
    private String title;
    private String description;
}
