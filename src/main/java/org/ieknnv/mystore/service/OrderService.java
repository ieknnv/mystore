package org.ieknnv.mystore.service;

import java.util.List;

import org.ieknnv.mystore.dto.CartItemDetailDto;
import org.ieknnv.mystore.dto.OrderDto;
import org.ieknnv.mystore.entity.Order;

import reactor.core.publisher.Mono;

public interface OrderService {

    Mono<Order> placeOrder(long userId, List<CartItemDetailDto> cartItems);

    Mono<List<OrderDto>> getOrders(long userId);

    Mono<OrderDto> getOrder(long orderId);
}
