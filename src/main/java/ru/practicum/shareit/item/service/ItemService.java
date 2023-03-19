package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPatch;

import java.util.List;

public interface ItemService {

    List<Item> getAllItemsOfUser(long userId);

    Item getItemById(long userId, long itemId);

    Item saveItem(long userId, ItemDto itemDto);

    Item updateItem(long userId, ItemDtoPatch itemDtoPatch, Long itemId);

    List<Item> searchItem(long userId, String text);
}
