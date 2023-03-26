package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.LastBookingDto;
import ru.practicum.shareit.booking.dto.NextBookingDto;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
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
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemDto> getAllItemsOfUser(long userId) {
        handleOptionalUser(userRepository.findById(userId), userId);
        log.info("Возвращен список вещей пользователя с id = {}", userId);
        return itemRepository.findAllByOwnerIsOrderById(userId).stream()
                .map(x -> mapWithBookingsAndComments(x, true))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        log.info("Поиск вещи с id={}", itemId);
        Item found = handleOptionalItem(itemRepository.findById(itemId), itemId);
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
            throw new UserNotFoundException("Пользователь с id=" + userId + " не является владельцем вещи с id=" + itemId);
        } else {
            Item item = ItemDtoMapper.patchToItem(itemDto, found, itemId);
            itemRepository.updateItemById(item.getName(), item.getDescription(), item.getAvailable(), itemId);
            log.info("Обновлена вещь " + item + " пользователя с id=" + userId);
            Item updated = handleOptionalItem(itemRepository.findById(itemId), itemId);
            return mapWithBookingsAndComments(updated, false);
        }
    }

    @Override
    public List<ItemDto> searchItem(long userId, String text) {
        log.info("Поиск вещи по запросу {}", text);
        handleOptionalUser(userRepository.findById(userId), userId);
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> foundList = itemRepository.searchByNameAndDescription(text.toLowerCase());
        log.info("Найдены вещи по запросу query = " + text + ": " + foundList);
        return foundList.stream()
                .map(x -> mapWithBookingsAndComments(x, false))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDtoOut addComment(long userId, CommentDtoIn commentDtoIn, long itemId) {
        log.info("Сохранение комментария " + commentDtoIn + " для вещи id = " + itemId);
        User user = handleOptionalUser(userRepository.findById(userId), userId);
        List<Booking> bookings = bookingRepository.findByItemAndBookerAndStatus(itemId, userId, BookingStatus.REJECTED);
        if (bookings.size() == 0) {
            throw new ValidationException("Бронирование пользователем id = " + userId + " вещи id = " + " не найдено");
        }
        handleOptionalItem(itemRepository.findById(itemId), itemId);
        Comment comment = ItemDtoMapper.toComment(commentDtoIn, itemId, userId, LocalDateTime.now());
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
        handleOptionalUser(userRepository.findById(item.getOwner()), item.getOwner());
        List<CommentDtoOut> commentDtoOuts = commentRepository.findAllByItemIdIsOrderByCreatedDesc(item.getId()).stream()
                .map(x -> ItemDtoMapper.toCommentDtoOut(x, handleOptionalUser(userRepository.findById(x.getAuthorId()), x.getAuthorId()).getName()))
                .collect(Collectors.toList());
        if (!owner) {
            itemDto = ItemDtoMapper.toItemDto(item, commentDtoOuts);
        } else {
            List<Booking> lastBooking = bookingRepository.findLastBooking(item.getId(), BookingStatus.REJECTED);
            List<Booking> nextBooking = bookingRepository.findNextBooking(item.getId(), BookingStatus.REJECTED);
            LastBookingDto lastBookingDto = (lastBooking.size() == 0) ? null : BookingDtoMapper.lastBookingDto(lastBooking.get(0));
            NextBookingDto nextBookingDto = (nextBooking.size() == 0) ? null : BookingDtoMapper.nextBookingDto(nextBooking.get(0));
            itemDto = ItemDtoMapper.toItemDto(item, lastBookingDto, nextBookingDto, commentDtoOuts);
        }
        return itemDto;
    }
}
