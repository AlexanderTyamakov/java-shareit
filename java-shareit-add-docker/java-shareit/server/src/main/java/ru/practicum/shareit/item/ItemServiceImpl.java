package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.LastBookingDto;
import ru.practicum.shareit.booking.dto.NextBookingDto;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.Pagination;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemDto> getAllItemsOfUser(long userId, int from, int size) {
        handleOptionalUser(userRepository.findById(userId), userId);
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pagination pageRequest = new Pagination(from, size, sort);
        List<Item> items = itemRepository.findAllByOwnerIs(pageRequest, userId);
        List<ItemDto> itemDtoList = new ArrayList<>();
        List<Booking> bookings = bookingRepository.findAllByItemInAndStatusIsNot(items, BookingStatus.REJECTED);
        List<Comment> comments = commentRepository.findAllByItemIdIn(items);
        Map<Item, LastBookingDto> lastBookingDtoMap = new HashMap<>();
        Map<Item, NextBookingDto> nextBookingDtoMap = new HashMap<>();
        Map<Item, List<CommentDtoOut>> commentDtoOutMap = new HashMap<>();
        for (Item item : items) {
            lastBookingDtoMap.put(item, bookings.stream()
                    .filter(x -> Objects.equals(x.getItem(), item))
                    .filter(x -> x.getStart().isBefore(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getStart).reversed()).map(BookingDtoMapper::lastBookingDto)
                    .findFirst().orElse(null));
            nextBookingDtoMap.put(item, bookings.stream()
                    .filter(x -> Objects.equals(x.getItem(), item))
                    .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getStart)).map(BookingDtoMapper::nextBookingDto)
                    .findFirst().orElse(null));
            commentDtoOutMap.put(item, comments.stream()
                    .filter(x -> Objects.equals(x.getItemId(), item))
                    .map(x -> ItemDtoMapper.toCommentDtoOut(x, x.getAuthorId().getName()))
                    .collect(Collectors.toList()));
        }
        for (Item item : items) {
            itemDtoList.add(ItemDtoMapper.toItemDto(item, lastBookingDtoMap.get(item), nextBookingDtoMap.get(item), commentDtoOutMap.get(item)));
        }
        log.info("Возвращен список вещей пользователя с id = {}", userId);
        return itemDtoList;
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        log.info("Поиск вещи с id={}", itemId);
        Item found = handleOptionalItem(itemRepository.findById(itemId), itemId);
        handleOptionalUser(userRepository.findById(userId), userId);
        log.info("Получена вещь с id=" + itemId + " пользователя с id=" + userId);
        ItemDto itemDto;
        if (userId == found.getOwner()) {
            itemDto = mapWithBookingsAndComments(found, true);
        } else {
            itemDto = mapWithBookingsAndComments(found, false);
        }
        return itemDto;
    }

    @Override
    public ItemDto saveItem(long userId, ItemDto itemDto) {
        log.info("Сохранение вещи пользователя с id={}", userId);
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Не заполнено поле available");
        }
        handleOptionalUser(userRepository.findById(userId), userId);
        Item saved = itemRepository.save(ItemDtoMapper.toItem(itemDto, userId));
        log.info("Сохранена вещь " + saved + " пользователя с id=" + userId);
        return mapWithBookingsAndComments(saved, false);
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        log.info("Изменения вещи с id={}", userId);
        Item found = handleOptionalItem(itemRepository.findById(itemId), itemId);
        if (found.getOwner() != userId) {
            throw new UserNotFoundException("Пользователь с id = " + userId + " не является владельцем вещи с id = " + itemId);
        } else {
            handleOptionalUser(userRepository.findById(userId), userId);
            Item item = ItemDtoMapper.patchToItem(itemDto, found, itemId);
            itemRepository.updateItemById(item.getName(), item.getDescription(), item.getAvailable(), itemId);
            log.info("Обновлена вещь " + item + " пользователя с id = " + userId);
            Item updated = handleOptionalItem(itemRepository.findById(itemId), itemId);
            return mapWithBookingsAndComments(updated, false);
        }
    }

    @Override
    public List<ItemDto> searchItem(long userId, String text, int from, int size) {
        log.info("Поиск вещи по запросу {}", text);
        handleOptionalUser(userRepository.findById(userId), userId);
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pagination pageRequest = new Pagination(from, size, sort);
        List<Item> foundList = itemRepository.searchByNameAndDescription(pageRequest, text.toLowerCase());
        log.info("Найдены вещи по запросу query = " + text + ": " + foundList);
        return foundList.stream()
                .map(x -> mapWithBookingsAndComments(x, false))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDtoOut addComment(long userId, CommentDtoIn commentDtoIn, long itemId) {
        log.info("Сохранение комментария " + commentDtoIn + " для вещи id = " + itemId);
        User user = handleOptionalUser(userRepository.findById(userId), userId);
        Item item = handleOptionalItem(itemRepository.findById(itemId), itemId);
        List<Booking> bookings = bookingRepository.findByItemAndBookerAndStatus(item, user, BookingStatus.REJECTED);
        if (bookings.size() == 0) {
            throw new ValidationException("Бронирование пользователем id = " + userId + " вещи id = " + itemId + " не найдено");
        }
        Comment comment = ItemDtoMapper.toComment(commentDtoIn, item, user, LocalDateTime.now());
        commentRepository.save(comment);
        Comment created = commentRepository.findFirstByOrderByIdDesc();
        return ItemDtoMapper.toCommentDtoOut(created, user.getName());
    }

    private User handleOptionalUser(Optional<User> user, long id) {
        if (user.isEmpty()) {
            throw new UserNotFoundException("Пользователь с id = " + id + " не найден");
        }
        return user.orElseThrow();
    }

    private Item handleOptionalItem(Optional<Item> item, long id) {
        if (item.isEmpty()) {
            throw new ItemNotFoundException("Вещь с id = " + id + " не найдена");
        }
        return item.orElseThrow();
    }

    private ItemDto mapWithBookingsAndComments(Item item, boolean owner) {
        ItemDto itemDto;

        List<Comment> comments = commentRepository.findAllByItemIdIsOrderByCreatedDesc(item);
        List<CommentDtoOut> commentDtoOuts = comments.stream()
                .map(x -> ItemDtoMapper.toCommentDtoOut(x, x.getAuthorId().getName()))
                .collect(Collectors.toList());

        if (!owner) {
            itemDto = ItemDtoMapper.toItemDto(item, commentDtoOuts);
        } else {
            List<Booking> bookings = bookingRepository.findAllByItemIsAndStatusNot(item, BookingStatus.REJECTED);
            LastBookingDto lastBookingDto = bookings.stream()
                    .filter(x -> x.getStart().isBefore(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .map(BookingDtoMapper::lastBookingDto)
                    .findFirst().orElse(null);
            NextBookingDto nextBookingDto = bookings.stream()
                    .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getStart))
                    .map(BookingDtoMapper::nextBookingDto)
                    .findFirst().orElse(null);
            itemDto = ItemDtoMapper.toItemDto(item, lastBookingDto, nextBookingDto, commentDtoOuts);
        }
        return itemDto;
    }
}
