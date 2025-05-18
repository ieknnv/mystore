package org.ieknnv.mystore.service;

import java.io.IOException;

import org.ieknnv.mystore.dto.NewItemDto;
import org.ieknnv.mystore.exception.ItemProcessingException;
import org.ieknnv.mystore.mapper.ItemMapper;
import org.ieknnv.mystore.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

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
}
