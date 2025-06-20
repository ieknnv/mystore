package org.ieknnv.mystore.repository;

import org.ieknnv.mystore.entity.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ItemRepository extends ReactiveSortingRepository<Item, Long>, ReactiveCrudRepository<Item, Long> {

    @Query("SELECT item_image FROM items WHERE id = :id")
    Mono<byte[]> findItemImageById(Long id);

    @Query("""
    SELECT id, name, description, item_image, price
    FROM items
    WHERE LOWER(name) LIKE LOWER(CONCAT('%',:search,'%'))
       OR LOWER(description) LIKE LOWER(CONCAT('%',:search,'%'))
    """)
    Flux<Item> findAllBySearchLine(String search, Pageable pageable);

    Flux<Item> findAllBy(Pageable pageable);
}
