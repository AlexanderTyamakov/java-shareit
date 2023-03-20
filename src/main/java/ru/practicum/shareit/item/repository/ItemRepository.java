package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    List<Item> findAll(long userId);

    Optional<Item> getById(long itemId);

    Item save(long userId, ItemDto itemDto);

    Optional<Item> update(long userId, ItemDto itemDto, Long itemId);

    List<Item> search(String text);

}
