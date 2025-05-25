package org.ieknnv.mystore.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.ieknnv.mystore.dto.OrderDto;
import org.ieknnv.mystore.dto.OrderItemDto;
import org.ieknnv.mystore.entity.Order;
import org.ieknnv.mystore.entity.OrderItem;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static List<OrderDto> toDto(List<Order> orders) {
        return orders.stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    public static OrderDto toDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .items(toDto(order.getOrderItems()))
                .totalSum(getOrderTotalPrice(order))
                .build();
    }

    public static OrderItemDto toDto(OrderItem orderItem) {
        return OrderItemDto.builder()
                .id(orderItem.getId())
                .item(ItemMapper.toDto(orderItem.getItem(), orderItem.getQuantity()))
                .price(orderItem.getPrice())
                .count(orderItem.getQuantity())
                .build();
    }

    public static List<OrderItemDto> toDto(Set<OrderItem> orderItems) {
        ArrayList<OrderItemDto> list = new ArrayList<>();
        for (OrderItem oi : orderItems) {
            list.add(toDto(oi));
        }
        return list;
    }

    private static BigDecimal getOrderTotalPrice(Order order) {
        return order.getOrderItems().stream().map(oi -> oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
