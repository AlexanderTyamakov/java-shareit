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
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
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
        if (userRepository.getById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        }
        log.info("Возвращен список вещей пользователя с id={}", userId);
        return itemRepository.findAll(userId).stream()
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        log.info("Поиск вещи с id={}", itemId);
        Optional<Item> found = itemRepository.getById(itemId);
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
        if (userRepository.getById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        }
        Item saved = itemRepository.save(userId, itemDto);
        log.info("Сохранена вещь " + saved + " пользователя с id=" + userId);
        return ItemDtoMapper.toItemDto(saved);
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, Long itemId) {
        log.info("Изменения вещи с id={}", userId);
        Optional<Item> found = itemRepository.getById(itemId);
        if (found.isEmpty()) {
            throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
        }
        Optional<Item> updated = itemRepository.update(userId, itemDto, itemId);
        if (updated.isEmpty()) {
            throw new UserNotFoundException("Пользователь с id=" + userId + " не является владельцем вещи с id=" + itemId);
        } else log.info("Обновлена вещь " + updated.get() + " пользователя с id=" + userId);
        return ItemDtoMapper.toItemDto(updated.get());
    }

    @Override
    public List<ItemDto> searchItem(long userId, String text) {
        log.info("Поиск вещи по запросу {}", text);
        if (userRepository.getById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        }
        List<Item> foundList = itemRepository.search(text.toLowerCase());
        log.info("Найдены вещи по запросу query=" + text + ": " + foundList);
        return foundList.stream()
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());

    }
}
