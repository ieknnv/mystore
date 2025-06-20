package org.ieknnv.mystore.mapper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.stream.Collectors;

import org.ieknnv.mystore.dto.ItemDto;
import org.ieknnv.mystore.dto.OrderDto;
import org.ieknnv.mystore.dto.OrderItemDetailDto;
import org.ieknnv.mystore.dto.OrderItemDto;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderDto toDto(long orderId, Collection<OrderItemDetailDto> orderItems) {
        return OrderDto.builder()
                .id(orderId)
                .items(orderItems
                        .stream()
                        .map(oi -> OrderItemDto.builder()
                                .id(oi.getId())
                                .item(ItemDto.builder()
                                        .id(oi.getItemId())
                                        .title(oi.getTitle())
                                        .description(oi.getDescription())
                                        .price(oi.getPrice())
                                        .count(oi.getQuantity())
                                        .build())
                                .count(oi.getQuantity())
                                .price(oi.getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .totalSum(getOrderTotalPrice(orderItems))
                .build();
    }

    private static BigDecimal getOrderTotalPrice(Collection<OrderItemDetailDto> orderItems) {
        return orderItems.stream().map(oi -> oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
