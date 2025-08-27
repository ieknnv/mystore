package org.ieknnv.mystore.repository;

import org.ieknnv.mystore.entity.Cart;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface CartRepository extends ReactiveCrudRepository<Cart, Long> {

    @Query("""
            SELECT id, user_id
            FROM carts
            WHERE user_id=:userId
            """)
    Mono<Cart> findByUserId(long userId);

    @Modifying
    @Query("""
            DELETE FROM cart_items
            WHERE cart_id=:cartId
            """)
    Mono<Void> clearCart(long cartId);
}
