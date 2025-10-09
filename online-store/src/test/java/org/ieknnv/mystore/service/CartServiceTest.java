package org.ieknnv.mystore.service;

import org.ieknnv.mystore.dto.CartItemDetailDto;
import org.ieknnv.mystore.dto.CartPageDto;
import org.ieknnv.mystore.entity.Cart;
import org.ieknnv.mystore.entity.Order;
import org.ieknnv.mystore.enums.PaymentServiceError;
import org.ieknnv.mystore.repository.CartItemRepository;
import org.ieknnv.mystore.repository.CartRepository;
import org.ieknnv.payment.client.api.BalanceApi;
import org.ieknnv.payment.client.api.PaymentApi;
import org.ieknnv.payment.client.model.Balance;
import org.ieknnv.payment.client.model.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {CartServiceImpl.class})
public class CartServiceTest {

    @MockitoBean
    private CartRepository cartRepository;

    @MockitoBean
    private CartItemRepository cartItemRepository;

    @MockitoBean
    private BalanceApi balanceApi;

    @MockitoBean
    private PaymentApi paymentApi;

    @Autowired
    private CartService cartService;

    @MockitoBean
    private OrderService orderService;

    @Test
    void getCartForUser_ShouldReturnCartPageDto_WhenEverythingOk() {
        // given
        long userId = 1L;
        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUserId(userId);

        CartItemDetailDto item1 = CartItemDetailDto.builder()
                .id(1L)
                .title("Item A")
                .price(BigDecimal.valueOf(100))
                .build();
        CartItemDetailDto item2 = CartItemDetailDto.builder()
                .id(2L)
                .title("Item B")
                .price(BigDecimal.valueOf(200))
                .build();

        Balance balance = new Balance().userId(userId).amount(500.0);
        ResponseEntity<Balance> response = ResponseEntity.ok(balance);

        // mock behavior
        when(cartRepository.findByUserId(userId)).thenReturn(Mono.just(cart));
        when(cartItemRepository.findCartItemDetailByCart(cart.getId()))
                .thenReturn(Flux.just(item1, item2));
        when(balanceApi.getBalanceWithHttpInfo(userId)).thenReturn(Mono.just(response));

        // when
        Mono<CartPageDto> result = cartService.getCartForUser(userId);

        // then
        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertNotNull(dto);
                    assertEquals(BigDecimal.valueOf(500.0), dto.getUserBalance());
                    assertTrue(dto.isEnablePayment());
                    assertNull(dto.getPaymentError());
                    assertEquals(2, dto.getItemDtoList().size());
                })
                .verifyComplete();
    }

    @Test
    void getCartForUser_ShouldReturnErrorBalance_WhenApiThrowsWebClientException() {
        // given
        long userId = 1L;
        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUserId(userId);

        CartItemDetailDto cartItem = CartItemDetailDto.builder()
                .id(100L)
                .cartId(cart.getId())
                .itemId(55L)
                .title("Test Item")
                .description("Sample item in cart")
                .price(BigDecimal.valueOf(99.99))
                .quantity(2L)
                .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Mono.just(cart));
        when(cartItemRepository.findCartItemDetailByCart(cart.getId())).thenReturn(Flux.just(cartItem));
        when(balanceApi.getBalanceWithHttpInfo(anyLong()))
                .thenReturn(Mono.error(new WebClientResponseException(500, "Server Error", null, null, null)));

        // when
        Mono<CartPageDto> result = cartService.getCartForUser(userId);

        // then
        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertEquals(PaymentServiceError.UNEXPECTED_ERROR, dto.getPaymentError());
                    assertFalse(dto.isEnablePayment());
                })
                .verifyComplete();
    }

    @Test
    void buyCart_ShouldMakePaymentAndPlaceOrderSuccessfully() {
        // given
        long userId = 1L;

        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUserId(userId);

        CartItemDetailDto item1 = CartItemDetailDto.builder()
                .id(101L)
                .cartId(cart.getId())
                .itemId(1L)
                .title("Phone A")
                .price(BigDecimal.valueOf(500))
                .quantity(2L)
                .build();

        CartItemDetailDto item2 = CartItemDetailDto.builder()
                .id(102L)
                .cartId(cart.getId())
                .itemId(2L)
                .title("Phone B")
                .price(BigDecimal.valueOf(300))
                .quantity(1L)
                .build();

        List<CartItemDetailDto> cartItems = List.of(item1, item2);

        Order expectedOrder = new Order();
        expectedOrder.setId(999L);
        expectedOrder.setUserId(userId);

        // mocks
        when(cartRepository.findByUserId(userId)).thenReturn(Mono.just(cart));
        when(cartItemRepository.findCartItemDetailByCart(cart.getId())).thenReturn(Flux.fromIterable(cartItems));

        when(paymentApi.makePayment(any(Payment.class))).thenReturn(Mono.empty());
        when(orderService.placeOrder(eq(userId), anyList())).thenReturn(Mono.just(expectedOrder));
        when(cartRepository.clearCart(cart.getId())).thenReturn(Mono.empty());

        // when
        Mono<Order> result = cartService.buyCart(userId);

        // then
        StepVerifier.create(result)
                .assertNext(order -> {
                    assertNotNull(order);
                    assertEquals(expectedOrder.getId(), order.getId());
                    assertEquals(userId, order.getUserId());
                })
                .verifyComplete();

        // verify interactions
        verify(cartRepository).findByUserId(userId);
        verify(cartItemRepository).findCartItemDetailByCart(cart.getId());
        verify(orderService).placeOrder(eq(userId), anyList());
        verify(cartRepository).clearCart(cart.getId());
    }
}
