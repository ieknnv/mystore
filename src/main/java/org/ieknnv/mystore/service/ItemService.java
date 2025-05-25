package org.ieknnv.mystore.service;

import java.util.Optional;

import org.ieknnv.mystore.dto.ItemDto;
import org.ieknnv.mystore.dto.MainPageItemsDto;
import org.ieknnv.mystore.dto.NewItemDto;
import org.springframework.data.domain.Pageable;

public interface ItemService {

    void addNewItem(NewItemDto newItemDto);

    Optional<byte[]> findImageByItemId(Long id);

    MainPageItemsDto getItems(Long userId, String search, Pageable pageable);

    ItemDto getItem(long userId, long itemId);
}
