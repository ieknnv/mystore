package org.ieknnv.mystore.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemDto {
    private long id;
    private String title;
    private String description;
    private BigDecimal price;
    private long count;
}
