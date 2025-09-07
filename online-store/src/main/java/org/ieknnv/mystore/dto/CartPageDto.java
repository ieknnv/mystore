package org.ieknnv.mystore.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CartPageDto {
    private List<ItemDto> itemDtoList;
    private BigDecimal total;
    private boolean cartEmpty;
}
