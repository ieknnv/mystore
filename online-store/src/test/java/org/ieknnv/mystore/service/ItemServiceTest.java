package org.ieknnv.mystore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.ieknnv.mystore.AbstractTestContainerTest;
import org.ieknnv.mystore.dto.MainPageItemsDto;
import org.ieknnv.mystore.entity.Item;
import org.ieknnv.mystore.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ItemServiceTest extends AbstractTestContainerTest {

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemCacheService itemCacheService;

    @Test
    void testGetItems() throws JsonProcessingException {
        var userId = 1L;
        var search = "Phone";
        var pageable = PageRequest.of(0, 10);
        // Prepare cached items
        var cachedItems = List.of(
                Item.builder()
                        .id(1L)
                        .name("Phone A")
                        .description("Some description A")
                        .price(BigDecimal.ONE)
                        .itemImage(new byte[0])
                        .build(),
                Item.builder()
                        .id(2L)
                        .name("Phone B")
                        .description("Some description B")
                        .price(BigDecimal.TWO)
                        .itemImage(new byte[0])
                        .build()
        );
        // Pre-fill real cache
        itemCacheService.putItemsToCache(search, pageable, cachedItems).block();
        // Mock repository - should NOT be called for cache hit
        when(itemRepository.findAllBySearchLine(anyString(), any()))
                .thenReturn(Flux.empty());
        when(itemRepository.count()).thenReturn(Mono.just((long) cachedItems.size()));
        // Mock cart service
        when(cartService.getCartItemsForUser(userId))
                .thenReturn(Mono.just(Map.of(1L, 1L, 2L, 2L)));
        // Call the service
        Mono<MainPageItemsDto> resultMono = itemService.getItems(userId, search, pageable);
        StepVerifier.create(resultMono)
                .assertNext(dto -> {
                    // Assert items chunked
                    assertEquals(1, dto.getItems().size()); // itemsPerLine = 3, only 2 items → 1 chunk
                    assertEquals("Phone A", dto.getItems().getFirst().getFirst().getTitle());
                    assertEquals(1L, dto.getItems().getFirst().getFirst().getCount());
                    assertEquals("Phone B", dto.getItems().getFirst().get(1).getTitle());
                    assertEquals(2L, dto.getItems().getFirst().get(1).getCount());
                    // Assert page total
                    assertEquals(2, dto.getPage().getTotalElements());
                })
                .verifyComplete();

        // Clear cache to simulate cache miss
        itemCacheService.evictAllItemsFromCache().block();
        // Mock repository to return items for cache miss
        var dbItems = List.of(
                Item.builder()
                        .id(2L)
                        .name("Phone C")
                        .description("Some description C")
                        .price(BigDecimal.TEN)
                        .itemImage(new byte[0])
                        .build()
        );
        when(itemRepository.findAllBySearchLine(eq(search), any()))
                .thenReturn(Flux.fromIterable(dbItems));
        when(itemRepository.count()).thenReturn(Mono.just((long) dbItems.size()));
        // Call service again
        Mono<MainPageItemsDto> resultMono2 = itemService.getItems(userId, search, pageable);
        StepVerifier.create(resultMono2)
                .assertNext(dto -> {
                    assertEquals(1, dto.getItems().size()); // one chunk
                    assertEquals("Phone C", dto.getItems().getFirst().getFirst().getTitle());
                    assertEquals(0L, dto.getItems().getFirst().getFirst().getCount());
                    assertEquals(1, dto.getPage().getTotalElements());
                })
                .verifyComplete();
    }
}
