package org.ieknnv.mystore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ieknnv.mystore.dto.CartItemDetailDto;
import org.ieknnv.mystore.dto.OrderDto;
import org.ieknnv.mystore.dto.OrderItemDetailDto;
import org.ieknnv.mystore.entity.Order;
import org.ieknnv.mystore.entity.OrderItem;
import org.ieknnv.mystore.mapper.OrderMapper;
import org.ieknnv.mystore.repository.OrderItemRepository;
import org.ieknnv.mystore.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public Mono<Order> placeOrder(long userId, List<CartItemDetailDto> cartItems) {
        var newOrder = new Order();
        newOrder.setUserId(userId);
        Mono<Order> order = orderRepository.save(newOrder);
        return order
                .flatMapMany(o -> {
                    List<OrderItem> orderItems = new ArrayList<>();
                    cartItems.forEach(cartItem -> {
                        var orderItem = new OrderItem();
                        orderItem.setOrderId(o.getId());
                        orderItem.setItemId(cartItem.getItemId());
                        orderItem.setPrice(cartItem.getPrice());
                        orderItem.setQuantity(cartItem.getQuantity());
                        orderItems.add(orderItem);
                    });
                    return orderItemRepository.saveAll(orderItems);
                })
                .then(order);
    }

    @Override
    public Mono<List<OrderDto>>getOrders(long userId) {
        Flux<Order> orders = orderRepository.findAllByUserId(userId);
        return orders
                .collectList()
                .flatMap(list -> {
                    var orderIds = list.stream()
                            .map(Order::getId)
                            .toList();
                    return orderItemRepository.findAllByOrders(orderIds)
                            .collectMultimap(OrderItemDetailDto::getOrderId);
                })
                .map(orderItemsMap -> {
                    List<OrderDto> orderDtos = new ArrayList<>();
                    orderItemsMap
                            .forEach((key, value) -> orderDtos.add(
                                    OrderMapper.toDto(key, value)
                            ));
                    return orderDtos;
                });
    }

    @Override
    public Mono<OrderDto> getOrder(long orderId) {
        return orderItemRepository.findAllByOrders(Collections.singletonList(orderId))
                .collectList()
                .map(list -> OrderMapper.toDto(orderId, list));
    }
}
