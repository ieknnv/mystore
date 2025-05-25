package org.ieknnv.mystore.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.ieknnv.mystore.dto.ItemDto;
import org.ieknnv.mystore.dto.MainPageItemsDto;
import org.ieknnv.mystore.dto.NewItemDto;
import org.ieknnv.mystore.entity.Item;
import org.ieknnv.mystore.exception.ItemProcessingException;
import org.ieknnv.mystore.mapper.ItemMapper;
import org.ieknnv.mystore.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final CartService cartService;
    private final ItemRepository itemRepository;

    @Value("${application.itemsPerLine:3}")
    private int itemsPerLine;

    @Override
    @Transactional
    public void addNewItem(NewItemDto newItemDto) {
        try {
            var newItem = ItemMapper.toEntity(newItemDto);
            itemRepository.save(newItem);
        } catch (IOException e) {
            throw new ItemProcessingException("Can not get item image bytes", e);
        }
    }

    @Override
    public Optional<byte[]> findImageByItemId(Long id) {
        return itemRepository.findItemImageById(id);
    }

    @Override
    public MainPageItemsDto getItems(Long userId, String search, Pageable pageable) {
        var itemPage = StringUtils.isEmpty(search) ? itemRepository.findAll(pageable) :
                itemRepository.findAllBySearchLine(search, pageable);
        var items = itemPage.getContent();
        return MainPageItemsDto.builder()
                .items(chunkItems(items, cartService.getCartItemsForUser(userId)))
                .page(itemPage)
                .build();
    }

    @Override
    public ItemDto getItem(long userId, long itemId) {
        Item item = itemRepository
                .findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("item not found"));
        var itemCount = cartService.getCartItemsForUser(userId);
        return ItemMapper.toDto(item, itemCount.getOrDefault(item, 0L));
    }

    private List<List<ItemDto>> chunkItems(List<Item> items, Map<Item, Long> itemCount) {
        List<List<ItemDto>> chunks = new ArrayList<>();
        int listSize = items.size();
        for (int i = 0; i < listSize; i += itemsPerLine) {
            int end = Math.min(listSize, i + itemsPerLine);
            List<Item> subList = new ArrayList<>(items.subList(i, end));
            chunks.add(subList.stream()
                    .map(item -> ItemMapper.toDto(item, itemCount.getOrDefault(item, 0L)))
                    .collect(Collectors.toList()));
        }
        return chunks;
    }
}
