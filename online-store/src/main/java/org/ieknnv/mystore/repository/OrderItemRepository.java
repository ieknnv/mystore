package org.ieknnv.mystore.repository;

import org.ieknnv.mystore.dto.OrderItemDetailDto;
import org.ieknnv.mystore.entity.OrderItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

@Repository
public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, Long> {

    @Query("""
            SELECT oi.id AS id, oi.order_id, oi.item_id, oi.quantity AS quantity, oi.price AS price,
                   i.name AS title, i.description AS description
            FROM order_items AS oi
            JOIN items AS i ON oi.item_id = i.id
            WHERE order_id IN (:orderIds)
            """)
    Flux<OrderItemDetailDto> findAllByOrders(Iterable<Long> orderIds);
}
