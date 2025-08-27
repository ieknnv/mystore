package org.ieknnv.mystore.service;

import org.ieknnv.mystore.dto.ItemDto;
import org.ieknnv.mystore.dto.MainPageItemsDto;
import org.ieknnv.mystore.dto.NewItemDto;
import org.springframework.data.domain.Pageable;

import reactor.core.publisher.Mono;

public interface ItemService {

    Mono<Void> addNewItem(NewItemDto newItemDto);

    Mono<byte[]> findImageByItemId(Long id);

    Mono<MainPageItemsDto> getItems(Long userId, String search, Pageable pageable);

    Mono<ItemDto> getItem(long userId, long itemId);
}
