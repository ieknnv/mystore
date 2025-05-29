package org.ieknnv.mystore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.ieknnv.mystore.dto.CartPageDto;
import org.ieknnv.mystore.dto.ItemDto;
import org.ieknnv.mystore.entity.Cart;
import org.ieknnv.mystore.entity.CartItem;
import org.ieknnv.mystore.entity.Item;
import org.ieknnv.mystore.entity.Order;
import org.ieknnv.mystore.entity.User;
import org.ieknnv.mystore.enums.CartAction;
import org.ieknnv.mystore.mapper.ItemMapper;
import org.ieknnv.mystore.repository.CartRepository;
import org.ieknnv.mystore.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = {CartServiceImpl.class})
class CartServiceTest {

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CartRepository cartRepository;

    @MockitoBean
    private ItemRepository itemRepository;

    @Autowired
    private CartService cartService;

    @Test
    void updateCartTest() {
        long userId = 1L;
        long itemId = 100L;
        Cart cart = new Cart();
        cart.setCartItems(new HashSet<>());
        Item item = new Item();
        item.setPrice(new BigDecimal("15.00"));

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        cartService.updateCart(userId, itemId, CartAction.PLUS);

        verify(cartRepository).save(cart);
        assertEquals(1, cart.getCartItems().size());

        CartItem addedCartItem = cart.getCartItems().iterator().next();
        assertEquals(item, addedCartItem.getItem());
        assertEquals(1, addedCartItem.getQuantity());
    }

    @Test
    void getCartItemsForUserTest() {
        long userId = 1L;
        Cart cart = new Cart();
        Set<CartItem> cartItems = new HashSet<>();
        Item item = new Item();
        item.setPrice(new BigDecimal("20.00"));
        CartItem cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(3);
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        Map<Item, Long> result = cartService.getCartItemsForUser(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(3L, result.get(item));
    }

    @Test
    void getCartForUserTest() {
        long userId = 1L;
        Cart cart = new Cart();
        Set<CartItem> cartItems = new HashSet<>();
        Item item = new Item();
        item.setPrice(new BigDecimal("10.00"));
        CartItem cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(3);
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        List<ItemDto> dtoList = List.of(ItemDto.builder().title("Item").build());
        try (MockedStatic<ItemMapper> mocked = mockStatic(ItemMapper.class)) {
            mocked.when(() -> ItemMapper.toDto(cartItems)).thenReturn(dtoList);
            CartPageDto result = cartService.getCartForUser(userId);
            assertNotNull(result);
            assertFalse(result.isCartEmpty());
            assertEquals(dtoList, result.getItemDtoList());
            assertEquals(new BigDecimal("30.00"), result.getTotal());
        }
    }

    @Test
    void buyCartTest() {
        long userId = 1L;
        Cart cart = new Cart();
        Set<CartItem> cartItems = new HashSet<>();
        Item item = new Item();
        item.setPrice(new BigDecimal("5.00"));
        CartItem cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(2);
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        User user = new User();
        when(userService.getUser(userId)).thenReturn(user);
        Order order = new Order();
        order.setId(500L);
        when(orderService.placeOrder(user, cartItems)).thenReturn(order);

        long orderId = cartService.buyCart(userId);

        assertEquals(500L, orderId);
        assertTrue(cart.getCartItems().isEmpty());
        verify(cartRepository).save(cart);
    }
}

