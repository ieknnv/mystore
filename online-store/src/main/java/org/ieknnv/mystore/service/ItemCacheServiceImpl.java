package org.ieknnv.mystore.service;

import lombok.RequiredArgsConstructor;
import org.ieknnv.mystore.entity.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemCacheServiceImpl implements ItemCacheService {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    @Value("${application.redis.ttlInMinutes:1}")
    private int ttlInMinutes;

    @SuppressWarnings("unchecked")
    @Override
    public Mono<List<Item>> findItemsInCache(String search, Pageable pageable) {
        String key = buildKey(search, pageable);
        return redisTemplate.opsForValue()
                .get(key)
                .map(obj -> (List<Item>) obj); // cast Object -> List<Item>
    }

    @Override
    public Mono<Boolean> putItemsToCache(String search, Pageable pageable, List<Item> items) {
        String key = buildKey(search, pageable);
        return redisTemplate.opsForValue()
                .set(key, items, Duration.ofMinutes(ttlInMinutes));
    }

    @Override
    public Mono<Boolean> evictAllItemsFromCache() {
        return redisTemplate
                .keys("items:*") // match all item cache keys
                .collectList()
                .flatMapMany(Flux::fromIterable)
                .flatMap(redisTemplate::delete)
                .then(Mono.just(true));
    }

    // Build a unique Redis key for search + pagination
    private String buildKey(String search, Pageable pageable) {
        String sanitizedSearch = (search == null || search.isBlank()) ? "ALL" : search.trim();
        return String.format("items:%s:page=%d:size=%d:sort=%s",
                sanitizedSearch,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());
    }
}
