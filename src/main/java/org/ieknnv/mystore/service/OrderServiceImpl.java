package org.ieknnv.mystore.service;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.ieknnv.mystore.dto.OrderDto;
import org.ieknnv.mystore.entity.CartItem;
import org.ieknnv.mystore.entity.Order;
import org.ieknnv.mystore.entity.OrderItem;
import org.ieknnv.mystore.entity.User;
import org.ieknnv.mystore.mapper.OrderMapper;
import org.ieknnv.mystore.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public Order placeOrder(User user, Set<CartItem> cartItems) {
        var newOrder = new Order();
        newOrder.setUser(user);
        Set<OrderItem> orderItems = new HashSet<>();
        cartItems.forEach(cartItem -> {
            var orderItem = new OrderItem();
            orderItem.setOrder(newOrder);
            orderItem.setItem(cartItem.getItem());
            orderItem.setPrice(cartItem.getItem().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItems.add(orderItem);
        });
        newOrder.setOrderItems(orderItems);
        orderRepository.save(newOrder);
        return newOrder;
    }

    @Override
    public List<OrderDto> getOrders(long userId) {
        var orders = orderRepository.findAllByUserId(userId);
        return OrderMapper.toDto(orders);
    }

    @Override
    public OrderDto getOrder(long orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));
        return OrderMapper.toDto(order);
    }
}
