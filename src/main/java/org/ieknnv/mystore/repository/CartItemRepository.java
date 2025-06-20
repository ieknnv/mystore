package org.ieknnv.mystore.repository;

import org.ieknnv.mystore.dto.CartItemDetailDto;
import org.ieknnv.mystore.entity.CartItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CartItemRepository extends ReactiveCrudRepository<CartItem, Long> {

    @Query("""
            SELECT id,cart_id, item_id,quantity
            FROM cart_items
            WHERE cart_id=:cartId
            """)
    Flux<CartItem> findByCart(long cartId);

    @Query("""
            SELECT id, cart_id, item_id, quantity
            FROM cart_items
            WHERE cart_id=:cartId AND item_id=:itemId
            """)
    Mono<CartItem> findByCartAndItem(long cartId, long itemId);

    @Query("""
            DELETE FROM cart_items
            WHERE cart_id=:cartId AND item_id=:itemId
            """)
    Mono<Void> deleteByCartAndItem(long cartId, long itemId);

    @Query("""
            SELECT ci.id AS id, ci.cart_id AS cartId, ci.item_id AS itemId, it.name AS title,
                it.description AS description, it.price AS price, ci.quantity AS quantity
            FROM cart_items AS ci
            JOIN items AS it ON it.id=ci.item_id
            WHERE cart_id=:cartId
            ORDER BY ci.id ASC
            """)
    Flux<CartItemDetailDto> findCartItemDetailByCart(long cartId);
}
