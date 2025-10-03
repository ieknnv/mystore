package org.ieknnv.mystore.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.ieknnv.mystore.dto.ItemDto;
import org.ieknnv.mystore.dto.MainPageItemsDto;
import org.ieknnv.mystore.dto.NewItemDto;
import org.ieknnv.mystore.entity.Item;
import org.ieknnv.mystore.mapper.ItemMapper;
import org.ieknnv.mystore.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final CartService cartService;
    private final ItemCacheService itemCacheService;
    private final ItemRepository itemRepository;

    @Value("${application.itemsPerLine:3}")
    private int itemsPerLine;

    @Override
    @Transactional
    public Mono<Void> addNewItem(NewItemDto newItemDto) {
        return filePartToBytes(newItemDto.getImage())
                .map(imageBytes -> ItemMapper.toEntity(newItemDto, imageBytes))
                .flatMap(itemRepository::save)
                .flatMap(saved -> itemCacheService.evictAllItemsFromCache())
                .then();
    }

    @Override
    public Mono<byte[]> findImageByItemId(Long id) {
        return itemRepository.findById(id).map(Item::getItemImage);
    }

    @Override
    public Mono<MainPageItemsDto> getItems(Long userId, String search, Pageable pageable) {
        Flux<Item> items = itemCacheService.findItemsInCache(search, pageable)
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(
                        Flux.defer(() -> {
                            Flux<Item> dbItems = StringUtils.isEmpty(search) ? itemRepository.findAllBy(pageable) :
                                    itemRepository.findAllBySearchLine(search, pageable);
                            return dbItems
                                    .collectList()
                                    .flatMap(itemList ->
                                            itemCacheService.putItemsToCache(search, pageable, itemList)
                                                    .thenReturn(itemList))
                                    .flatMapMany(Flux::fromIterable);
                        })
                );
        Mono<Long> totalItems = itemRepository.count();
        Mono<Map<Long, Long>> itemCount = cartService.getCartItemsForUser(userId);
        return itemCount.flatMap(countMap ->
                items
                        .map(item -> ItemMapper.toDto(item, countMap.getOrDefault(item.getId(), 0L)))
                        .buffer(itemsPerLine)
                        .collectList()
                        .zipWith(totalItems)
                        .map(tuple -> {
                            List<List<ItemDto>> chunkedItems = tuple.getT1();
                            long total = tuple.getT2();

                            Page<Item> page = new PageImpl<>(List.of(), pageable, total);

                            return MainPageItemsDto.builder()
                                    .items(chunkedItems)
                                    .page(page)
                                    .build();
                        }));
    }

    @Override
    public Mono<ItemDto> getItem(long userId, long itemId) {
        Mono<Item> item = itemRepository
                .findById(itemId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("item not found")));
        Mono<Map<Long, Long>> itemCount = cartService.getCartItemsForUser(userId);
        return itemCount
                .flatMap(countMap ->
                        item.map(i -> ItemMapper.toDto(i, countMap.getOrDefault(i.getId(), 0L))));
    }

    private Mono<byte[]> filePartToBytes(FilePart filePart) {
        return DataBufferUtils.join(filePart.content())
                .map(buffer -> {
                    byte[] bytes = new byte[buffer.readableByteCount()];
                    buffer.read(bytes);
                    return bytes;
                });
    }
}
