package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPatch;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public List<Item> getAllItemsOfUser(long userId) {
        log.info("Возвращен список вещей пользователя с id={}", userId);
        return itemRepository.findAll(userId);
    }

    @Override
    public Item getItemById(long userId, long itemId) {
        log.info("Поиск вещи с id={}", itemId);
        return itemRepository.getById(userId, itemId);
    }

    @Override
    public Item saveItem(long userId, ItemDto itemDto) {
        log.info("Сохранение вещи пользователя с id={}", userId);
        return itemRepository.save(userId, itemDto);
    }

    @Override
    public Item updateItem(long userId, ItemDtoPatch itemDtoPatch, Long itemId) {
        log.info("Изменения вещи с id={}", userId);
        return itemRepository.update(userId, itemDtoPatch, itemId);
    }

    @Override
    public List<Item> searchItem(long userId, String text) {
        log.info("Поиск вещи по запросу {}", text);
        return itemRepository.search(userId, text.toLowerCase());
    }
}
