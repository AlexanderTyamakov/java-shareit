package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllItemsOfUser(long userId);

    ItemDto getItemById(long userId, long itemId);

    ItemDto saveItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, ItemDto itemDto, Long itemId);

    List<ItemDto> searchItem(long userId, String text);
}
