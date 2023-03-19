package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPatch;

import java.util.List;

public interface ItemRepository {

    List<Item> findAll(long userId);

    Item getById(long userId, long itemId);

    Item save(long userId, ItemDto itemDto);

    Item update(long userId, ItemDtoPatch itemDtoPatch, Long itemId);

    List<Item> search(long userId, String text);

}
