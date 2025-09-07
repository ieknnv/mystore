package org.ieknnv.mystore.repository;

import org.ieknnv.mystore.entity.Order;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {

    @Query("""
            SELECT id, user_id
            FROM orders
            WHERE user_id=:userId
            ORDER BY id DESC
            """)
    Flux<Order> findAllByUserId(long userId);
}
