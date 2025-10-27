package org.ieknnv.mystore.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderDto {
    private long id;
    private List<OrderItemDto> items;
    private BigDecimal totalSum;
}
