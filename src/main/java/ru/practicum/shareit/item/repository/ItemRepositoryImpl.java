package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long itemRepositoryId;

    @Override
    public List<Item> findAll(long userId) {
        return items.values().stream()
                .filter(x -> x.getOwner() == userId)
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> getById(long itemId) {
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
    public List<Item> search(String text) {
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
