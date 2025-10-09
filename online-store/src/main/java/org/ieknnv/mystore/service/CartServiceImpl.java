package org.ieknnv.mystore.service;

import lombok.RequiredArgsConstructor;
import org.ieknnv.mystore.dto.CartItemDetailDto;
import org.ieknnv.mystore.dto.CartPageDto;
import org.ieknnv.mystore.dto.UserBalanceDto;
import org.ieknnv.mystore.entity.Cart;
import org.ieknnv.mystore.entity.CartItem;
import org.ieknnv.mystore.entity.Order;
import org.ieknnv.mystore.enums.CartAction;
import org.ieknnv.mystore.enums.PaymentServiceError;
import org.ieknnv.mystore.exception.PaymentException;
import org.ieknnv.mystore.mapper.ItemMapper;
import org.ieknnv.mystore.repository.CartItemRepository;
import org.ieknnv.mystore.repository.CartRepository;
import org.ieknnv.payment.client.api.BalanceApi;
import org.ieknnv.payment.client.api.PaymentApi;
import org.ieknnv.payment.client.model.Balance;
import org.ieknnv.payment.client.model.Payment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final OrderService orderService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BalanceApi balanceApi;
    private final PaymentApi paymentApi;

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

        Mono<UserBalanceDto> userBalance = balanceApi.getBalanceWithHttpInfo(userId)
                .map(response -> {
                    Balance balance = response.getBody();
                    return UserBalanceDto.builder()
                            .userId(userId)
                            .amount(BigDecimal.valueOf(balance.getAmount()))
                            .build();
                })
                .onErrorResume(throwable -> {
                    UserBalanceDto userBalanceDto = new UserBalanceDto();
                    userBalanceDto.setUserId(userId);
                    userBalanceDto.setAmount(null);
                    if (throwable instanceof WebClientResponseException) {
                        userBalanceDto.setError(PaymentServiceError.UNEXPECTED_ERROR);
                    } else {
                        userBalanceDto.setError(PaymentServiceError.SERVICE_UNAVAILABLE);
                    }
                    return Mono.just(userBalanceDto);
                });

        return cartItems
                .collectList()
                .zipWith(userBalance)
                .map(tuple -> {
                    if (tuple.getT1().isEmpty()) {
                        return CartPageDto.builder()
                                .itemDtoList(Collections.emptyList())
                                .cartEmpty(true)
                                .total(BigDecimal.ZERO)
                                .build();
                    }
                    BigDecimal total = tuple.getT1().stream()
                            .map(ci -> ci.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    var userBalanceDto = tuple.getT2();
                    var userBalanceAvailable = userBalanceDto.getAmount() != null;
                    var balance = userBalanceAvailable ? userBalanceDto.getAmount() : BigDecimal.ZERO;
                    boolean enablePayment;
                    PaymentServiceError error = null;
                    if (userBalanceDto.getError() != null) {
                        enablePayment = false;
                        error = userBalanceDto.getError();
                    } else {
                        if (total.compareTo(balance) > 0) {
                            enablePayment = false;
                            error = PaymentServiceError.NOT_ENOUGH_MONEY;
                        } else {
                            enablePayment = true;
                        }
                    }
                    return CartPageDto.builder()
                            .itemDtoList(ItemMapper.toDto(tuple.getT1()))
                            .cartEmpty(false)
                            .total(total)
                            .userBalanceAvailable(userBalanceAvailable)
                            .userBalance(balance)
                            .enablePayment(enablePayment)
                            .paymentError(error)
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
                                .flatMap(cartItems -> {
                                    // Calculate total price
                                    BigDecimal total = cartItems.stream()
                                            .map(ci -> ci.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    // Make payment before proceeding with creating a new order and clearing user cart
                                    var payment = new Payment();
                                    payment.setUserId(userId);
                                    payment.setAmount(total.doubleValue());
                                    return paymentApi.makePayment(payment)
                                            .onErrorMap(ex -> new PaymentException(PaymentServiceError.UNEXPECTED_ERROR.getMessage(), ex))
                                            .then(orderService.placeOrder(userId, cartItems)
                                                    .flatMap(order ->
                                                            cartRepository.clearCart(cart.getId())
                                                                    .thenReturn(order)
                                                    ));
                                })
                );
    }
}
