package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.user.repository.UserRepository;

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
    public Optional<Item> getById(long userId, long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item save(long userId, ItemDto itemDto) {
        long id = getId();
        Item item = ItemDtoMapper.toItem(itemDto, id, userId);
        items.put(id, item);
        return item;
    }

    @Override
    public Optional<Item> update(long userId, ItemDto itemDto, Long itemId) {
        Item item;
        if (items.get(itemId).getOwner() != userId) {
            item = null;
        } else {
            item = ItemDtoMapper.patchToItem(itemDto, items.get(itemId), itemId, userId);
            items.put(itemId, item);
        }
        return Optional.ofNullable(item);
    }

    @Override
    public List<Item> search(long userId, String text) {
        userRepository.getById(userId);
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(x -> x.getAvailable().equals(true))
                .filter(x -> (x.getName().toLowerCase().contains(text) || (x.getDescription().toLowerCase().contains(text))))
                .collect(Collectors.toList());
    }

    private long getId() {
        return ++itemRepositoryId;
    }
}
