package org.ieknnv.mystore.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.ieknnv.mystore.dto.CartPageDto;
import org.ieknnv.mystore.entity.Cart;
import org.ieknnv.mystore.entity.CartItem;
import org.ieknnv.mystore.entity.Item;
import org.ieknnv.mystore.entity.User;
import org.ieknnv.mystore.enums.CartAction;
import org.ieknnv.mystore.exception.CartEmptyException;
import org.ieknnv.mystore.mapper.ItemMapper;
import org.ieknnv.mystore.repository.CartRepository;
import org.ieknnv.mystore.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final OrderService orderService;
    private final UserService userService;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public void updateCart(long userId, long itemId, CartAction cartAction) {
        final var cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("cart not found"));
        final var item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("item not found"));
        final var newCartItem = CartItem.builder()
                .cart(cart)
                .item(item)
                .build();
        final var existingCartItems = cart.getCartItems();
        switch (cartAction) {
            case PLUS -> {
                if (existingCartItems.contains(newCartItem)) {
                    final var existingCartItem = getCartItem(existingCartItems, newCartItem)
                            .orElseThrow(() -> new NoSuchElementException("cart item not found"));
                    existingCartItem.setQuantity(existingCartItem.getQuantity() + 1);
                } else {
                    newCartItem.setQuantity(1);
                    existingCartItems.add(newCartItem);
                }
            }
            case MINUS -> {
                if (existingCartItems.contains(newCartItem)) {
                    final var existingCartItem = getCartItem(existingCartItems, newCartItem)
                            .orElseThrow(() -> new NoSuchElementException("cart item not found"));
                    final var currentQuantity = existingCartItem.getQuantity();
                    if (currentQuantity > 1) {
                        existingCartItem.setQuantity(currentQuantity - 1);
                    } else {
                        existingCartItems.remove(newCartItem);
                    }
                } else {
                    return;
                }
            }
            case DELETE -> existingCartItems.remove(newCartItem);
        }
        cartRepository.save(cart);
    }

    @Override
    public Map<Item, Long> getCartItemsForUser(long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new NoSuchElementException("cart not found"));
        return cart.getCartItems()
                .stream()
                .collect(Collectors.toMap(CartItem::getItem, CartItem::getQuantity));
    }

    @Override
    public CartPageDto getCartForUser(long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new NoSuchElementException("cart not found"));
        var cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            return CartPageDto.builder()
                    .itemDtoList(Collections.emptyList())
                    .cartEmpty(true)
                    .total(BigDecimal.ZERO)
                    .build();
        }
        BigDecimal total = cartItems.stream()
                .map(ci -> ci.getItem().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartPageDto.builder()
                .itemDtoList(ItemMapper.toDto(cartItems))
                .cartEmpty(false)
                .total(total)
                .build();
    }

    @Override
    @Transactional
    public long buyCart(long userId) {
        var cart = cartRepository.findByUserId(userId).orElseThrow(() -> new NoSuchElementException("cart not found"));
        if (cart.getCartItems().isEmpty()) {
            throw new CartEmptyException("cart is empty");
        }
        User user = userService.getUser(userId);
        var newOrder = orderService.placeOrder(user, cart.getCartItems());
        cart.getCartItems().clear();
        cartRepository.save(cart);
        return newOrder.getId();
    }

    private Optional<CartItem> getCartItem(Set<CartItem> cartItems, CartItem cartItem) {
        return cartItems.stream()
                .filter(ci -> ci.equals(cartItem))
                .findFirst();
    }
}
