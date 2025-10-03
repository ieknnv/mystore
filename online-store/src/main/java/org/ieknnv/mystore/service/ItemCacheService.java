package org.ieknnv.mystore.service;

import org.ieknnv.mystore.entity.Item;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ItemCacheService {

    Mono<List<Item>> findItemsInCache(String search, Pageable pageable);

    Mono<Boolean> putItemsToCache(String search, Pageable pageable, List<Item> items);
}
