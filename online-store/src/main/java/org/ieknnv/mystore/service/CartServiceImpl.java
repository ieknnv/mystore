package org.ieknnv.mystore.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;

import org.ieknnv.mystore.dto.CartItemDetailDto;
import org.ieknnv.mystore.dto.CartPageDto;
import org.ieknnv.mystore.entity.Cart;
import org.ieknnv.mystore.entity.CartItem;
import org.ieknnv.mystore.entity.Order;
import org.ieknnv.mystore.enums.CartAction;
import org.ieknnv.mystore.mapper.ItemMapper;
import org.ieknnv.mystore.repository.CartItemRepository;
import org.ieknnv.mystore.repository.CartRepository;
import org.ieknnv.mystore.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final OrderService orderService;
    private final UserService userService;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public Mono<Void> updateCart(long userId, long itemId, CartAction cartAction) {
        final var cart = cartRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("cart not found")));
        final Mono<CartItem> cartItem = cart
                .flatMap(c -> cartItemRepository.findByCartAndItem(c.getId(), itemId));
        return switch (cartAction) {
            case PLUS -> cartItem
                    .switchIfEmpty(cart.flatMap(c ->
                            Mono.just(CartItem.builder().cartId(c.getId()).itemId(itemId).quantity(0).build())))
                    .map(ci -> {
                        ci.setQuantity(ci.getQuantity() + 1);
                        return ci;
                    })
                    .flatMap(cartItemRepository::save)
                    .then();
            case MINUS -> cartItem
                    .switchIfEmpty(Mono.error(new NoSuchElementException("cart item not found")))
                    .map(ci -> {
                        if (ci.getQuantity() > 1) {
                            ci.setQuantity(ci.getQuantity() - 1);
                        }
                        return ci;
                    })
                    .flatMap(cartItemRepository::save)
                    .then();
            case DELETE -> cartItem
                    .switchIfEmpty(Mono.error(new NoSuchElementException("cart item not found")))
                    .flatMap(ci -> cartItemRepository.deleteByCartAndItem(ci.getCartId(), ci.getItemId()))
                    .then();
        };
    }

    @Override
    public Mono<Map<Long, Long>> getCartItemsForUser(long userId) {
        return cartRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Cart not found")))
                .flatMap(cart ->
                        cartItemRepository.findByCart(cart.getId())
                                .collectMap(CartItem::getItemId, CartItem::getQuantity)
                );
    }

    @Override
    public Mono<CartPageDto> getCartForUser(long userId) {
        Mono<Cart> cart = cartRepository
                .findByUserId(userId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("cart not found")));
        Flux<CartItemDetailDto> cartItems =
                cart.flatMapMany(c -> cartItemRepository.findCartItemDetailByCart(c.getId()));
        return cartItems
                .collectList()
                .map(list -> {
                    if (list.isEmpty()) {
                        return CartPageDto.builder()
                                .itemDtoList(Collections.emptyList())
                                .cartEmpty(true)
                                .total(BigDecimal.ZERO)
                                .build();
                    }
                    BigDecimal total = list.stream()
                            .map(ci -> ci.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return CartPageDto.builder()
                            .itemDtoList(ItemMapper.toDto(list))
                            .cartEmpty(false)
                            .total(total)
                            .build();
                });
    }

    @Override
    @Transactional
    public Mono<Order> buyCart(long userId) {
        return cartRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("cart not found")))
                .flatMap(cart ->
                        cartItemRepository.findCartItemDetailByCart(cart.getId())
                                .collectList()
                                .flatMap(cartItems ->
                                        orderService.placeOrder(userId, cartItems)
                                                .flatMap(order ->
                                                        cartRepository.clearCart(cart.getId())
                                                                .thenReturn(order)
                                                )
                                )
                );
    }
}
