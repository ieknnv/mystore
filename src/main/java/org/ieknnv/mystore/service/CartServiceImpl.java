package org.ieknnv.mystore.service;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.ieknnv.mystore.entity.Cart;
import org.ieknnv.mystore.entity.CartItem;
import org.ieknnv.mystore.entity.Item;
import org.ieknnv.mystore.enums.CartAction;
import org.ieknnv.mystore.repository.CartRepository;
import org.ieknnv.mystore.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

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

    private Optional<CartItem> getCartItem(Set<CartItem> cartItems, CartItem cartItem) {
        return cartItems.stream()
                .filter(ci -> ci.equals(cartItem))
                .findFirst();
    }
}
