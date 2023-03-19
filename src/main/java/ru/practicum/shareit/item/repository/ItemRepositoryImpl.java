package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoPatch;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private final UserRepository userRepository;
    private final Map<Long, Item> items = new HashMap<>();
    private long itemRepositoryId;

    @Autowired
    public ItemRepositoryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<Item> findAll(long userId) {
        userRepository.getById(userId);
        log.info("Получены все вещи пользователя с id=" + userId);
        return items.values().stream()
                .filter(x -> x.getOwner() == userId)
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Item getById(long userId, long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
        }
        log.info("Получена вещь с id=" + itemId + " пользователя с id=" + userId);
        return items.get(itemId);
    }

    @Override
    public Item save(long userId, ItemDto itemDto) {
        userRepository.getById(userId);
        long id = getId();
        Item item = ItemDtoMapper.toItem(itemDto, id, userId);
        items.put(id, item);
        log.info("Сохранена вещь " + item + " пользователя с id=" + userId);
        return item;
    }

    @Override
    public Item update(long userId, ItemDtoPatch itemDtoPatch, Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
        }
        if (items.get(itemId).getOwner() != userId) {
            throw new ValidationException("Пользователь с id=" + userId + " не является владельцем вещи с id=" + itemId);
        }
        Item item = ItemDtoMapper.patchToItem(itemDtoPatch, items.get(itemId), itemId, userId);
        items.put(itemId, item);
        log.info("Обновлена вещь " + item + " пользователя с id=" + userId);
        return item;
    }

    @Override
    public List<Item> search(long userId, String text) {
        userRepository.getById(userId);
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> found = items.values().stream()
                .filter(x -> x.getAvailable().equals(true))
                .filter(x -> (x.getName().toLowerCase().contains(text) || (x.getDescription().toLowerCase().contains(text))))
                .collect(Collectors.toList());
        if (found.size() == 0) {
            throw new ItemNotFoundException("Не найдены вещи по запросу query=" + text);
        }
        log.info("Найдены вещи по запросу query=" + text + ": " + found);
        return found;
    }

    private long getId() {
        return ++itemRepositoryId;
    }
}
