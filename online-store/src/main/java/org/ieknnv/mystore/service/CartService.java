package org.ieknnv.mystore.service;

import java.util.Map;

import org.ieknnv.mystore.dto.CartPageDto;
import org.ieknnv.mystore.entity.Order;
import org.ieknnv.mystore.enums.CartAction;

import reactor.core.publisher.Mono;

public interface CartService {

    Mono<Void> updateCart(long userId, long itemId, CartAction cartAction);

    Mono<Map<Long, Long>> getCartItemsForUser(long userId);

    Mono<CartPageDto> getCartForUser(long userId);

    Mono<Order> buyCart(long userId);
}
