package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getAllItemsOfUser(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        }
        log.info("Возвращен список вещей пользователя с id={}", userId);
        return itemRepository.findAllByUser(userId).stream()
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        log.info("Поиск вещи с id={}", itemId);
        Optional<Item> found = itemRepository.findById(itemId);
        if (found.isEmpty()) {
            throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
        } else log.info("Получена вещь с id=" + itemId + " пользователя с id=" + userId);
        return ItemDtoMapper.toItemDto(found.get());
    }

    @Override
    public ItemDto saveItem(long userId, ItemDto itemDto) {
        log.info("Сохранение вещи пользователя с id={}", userId);
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Не заполнено поле available");
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        }
        Item saved = itemRepository.save(ItemDtoMapper.toItem(itemDto,userId));
        log.info("Сохранена вещь " + saved + " пользователя с id=" + userId);
        return ItemDtoMapper.toItemDto(saved);
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, Long itemId) {
        log.info("Изменения вещи с id={}", userId);
        Optional<Item> found = itemRepository.findById(itemId);
        if (found.isEmpty()) {
            throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
        } else if (found.get().getOwner() != userId) {
            throw new UserNotFoundException("Пользователь с id=" + userId + " не является владельцем вещи с id=" + itemId);
        } else {
            Item item = ItemDtoMapper.patchToItem(itemDto, found.get(), itemId);
            itemRepository.updateItemById(item.getName(),item.getDescription(),item.getAvailable(),itemId);
            log.info("Обновлена вещь " + item + " пользователя с id=" + userId);
            return ItemDtoMapper.toItemDto(item);
        }
    }

    @Override
    public List<ItemDto> searchItem(long userId, String text) {
        log.info("Поиск вещи по запросу {}", text);
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        }
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> foundList = itemRepository.searchByNameAndDescription(text.toLowerCase());
        log.info("Найдены вещи по запросу query=" + text + ": " + foundList);
        return foundList.stream()
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
