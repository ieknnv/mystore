package org.ieknnv.mystore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.ieknnv.mystore.entity.CartItem;
import org.ieknnv.mystore.entity.Item;
import org.ieknnv.mystore.entity.Order;
import org.ieknnv.mystore.entity.OrderItem;
import org.ieknnv.mystore.entity.User;
import org.ieknnv.mystore.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = {OrderServiceImpl.class})
class OrderServiceTest {

    @MockitoBean
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        Mockito.reset(orderRepository);
    }

    @Test
    void placeOrderTest() {
        User user = new User();
        Item item = new Item();
        item.setPrice(new BigDecimal("10.00"));
        CartItem cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(2);
        Set<CartItem> cartItems = Set.of(cartItem);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.placeOrder(user, cartItems);

        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();

        assertEquals(user, savedOrder.getUser());
        assertEquals(1, savedOrder.getOrderItems().size());
        OrderItem savedItem = savedOrder.getOrderItems().iterator().next();
        assertEquals(item, savedItem.getItem());
        assertEquals(2, savedItem.getQuantity());
        assertEquals(new BigDecimal("10.00"), savedItem.getPrice());
    }

    @Test
    void getNonExistingOrderTest() {
        long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> orderService.getOrder(orderId));
    }
}
