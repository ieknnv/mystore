package org.ieknnv.mystore.service;

import java.util.List;
import java.util.Set;

import org.ieknnv.mystore.dto.OrderDto;
import org.ieknnv.mystore.entity.CartItem;
import org.ieknnv.mystore.entity.Order;
import org.ieknnv.mystore.entity.User;

public interface OrderService {

    Order placeOrder(User user, Set<CartItem> cartItems);

    List<OrderDto> getOrders(long userId);

    OrderDto getOrder(long orderId);
}
