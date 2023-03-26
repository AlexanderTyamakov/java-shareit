package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
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
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDtoOut getBookingById(long userId, long bookingId) {
        log.info("Получение бронирования id = " + bookingId);
        handleOptionalUser(userRepository.findById(userId), userId);
        Booking booking = handleOptionalBooking(bookingRepository.findById(bookingId), bookingId);
        Item item = handleOptionalItem(itemRepository.findById(booking.getItem()), booking.getItem());
        if (booking.getBooker() != userId && item.getOwner() != userId) {
            throw new BookingNotFoundException("Запрос статуса бронирования поступил не от владельца вещи или автора заявки");
        }
        log.info("Получено бронирование id = " + bookingId);
        return BookingDtoMapper.toBookingDtoOut(booking, item);
    }

    @Override
    public List<BookingDtoOut> getBookingsOfUser(long userId, String query) {
        BookingState state;
        if (query == null) {
            state = BookingState.ALL;
        } else {
            try {
                state = BookingState.valueOf(query);
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Unknown state: " + query);
            }
        }
        log.info("Получение списка бронирования для пользователя id = " + userId + " по state = " + state);
        handleOptionalUser(userRepository.findById(userId), userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIsAndStatusIsOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIsAndStatusIsOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerAndCurrentOrderByStartDesc(userId);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerAndFutureOrderByStartDesc(userId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerAndPastOrderByStartDesc(userId);
                break;
            case ALL:
                bookings = bookingRepository.findAllByBookerIsOrderByStartDesc(userId);
                break;
        }
        if (bookings.size() == 0) {
            return new ArrayList<>();
        }
        log.info("Получен список бронирования для пользователя id = " + userId + " : " + bookings);
        return bookings.stream()
                .map(x -> BookingDtoMapper.toBookingDtoOut(x, handleOptionalItem(itemRepository.findById(x.getItem()),x.getItem())))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoOut> getBookingsOfOwner(long userId, String query) {
        BookingState state;
        if (query == null) {
            state = BookingState.ALL;
        } else {
            try {
                state = BookingState.valueOf(query);
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Unknown state: " + query);
            }
        }
        log.info("Получение списка бронирования для владельца id = " + userId + " по state = " + state);
        handleOptionalUser(userRepository.findById(userId), userId);
        List<Long> idOfOwnerItems = itemRepository.findAllByOwnerIsOrderById(userId).stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        if (idOfOwnerItems.size() == 0) {
            return new ArrayList<>();
        }
        List<Booking> bookings = new ArrayList<>();
           switch (state) {
            case REJECTED:
                bookings = bookingRepository.findAllByItemInAndStatusIsOrderByStartDesc(idOfOwnerItems, BookingStatus.REJECTED);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemInAndStatusIsOrderByStartDesc(idOfOwnerItems, BookingStatus.WAITING);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemsAndCurrentOrderByStartDesc(idOfOwnerItems);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemsAndFutureOrderByStartDesc(idOfOwnerItems);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemAndPastOrderByStartDesc(idOfOwnerItems);
                break;
            case ALL:
                bookings = bookingRepository.findAllByItemInOrderByStartDesc(idOfOwnerItems);
                break;
        }
        if (bookings.size() == 0) {
            return new ArrayList<>();
        }
        log.info("Получен список бронирования для владельца id = " + userId + " : " + bookings);
        return bookings.stream()
                .map(x -> BookingDtoMapper.toBookingDtoOut(x, handleOptionalItem(itemRepository.findById(x.getItem()),x.getItem())))
                .collect(Collectors.toList());

    }

    @Override
    public BookingDtoOut saveBooking(long userId, BookingDtoIn bookingDtoIn) {
        log.info("Добавление бронирования " + bookingDtoIn);
        if (bookingDtoIn.getEnd().isBefore(bookingDtoIn.getStart()) || bookingDtoIn.getEnd().isEqual(bookingDtoIn.getStart())) {
            throw new ValidationException("Дата конца бронирования раньше или равна дате начала");
        }
        handleOptionalUser(userRepository.findById(userId), userId);
        Item item = handleOptionalItem(itemRepository.findById(bookingDtoIn.getItemId()), bookingDtoIn.getItemId());
        if (!item.getAvailable()) {
            throw new ValidationException("Товар с id = " + item.getId() + " недоступен для бронирования");
        }
        if (userId == item.getOwner()) {
            throw new ItemNotFoundException("Запрошено бронирование " + item + " владельцем");
        }
        Booking saved = bookingRepository.save(BookingDtoMapper.toBooking(bookingDtoIn, userId));
        log.info("Сохранено бронирование " + saved + " пользователя с id = " + userId);
        return BookingDtoMapper.toBookingDtoOut(saved, item);
    }

    @Override
    public BookingDtoOut changeStatus(long userId, long bookingId, Boolean approved) {
        log.info("Обновление статуса бронирования с id = " + bookingId);
        handleOptionalUser(userRepository.findById(userId), userId);
        Booking booking = handleOptionalBooking(bookingRepository.findById(bookingId), bookingId);
        Item item = handleOptionalItem(itemRepository.findById(booking.getItem()), booking.getItem());
        if (item.getOwner() != userId) {
            throw new BookingNotFoundException("Запрос на изменение статуса бронирования поступил не от владельца вещи");
        }
        BookingStatus bookingStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        if (bookingStatus == BookingStatus.APPROVED && booking.getStatus() == BookingStatus.APPROVED) {
            throw new ValidationException("Бронирование id = " + booking + " уже было подтвреждено");
        }
        bookingRepository.changeStatusById(bookingStatus, bookingId);
        log.info("Обновлен статус бронирования с id = " + bookingId);
        Booking newBooking = handleOptionalBooking(bookingRepository.findById(bookingId), bookingId);
        return BookingDtoMapper.toBookingDtoOut(newBooking, item);
    }


    private void handleOptionalUser(Optional<User> user, long id) {
        if (user.isEmpty()) {
            throw new UserNotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    private Item handleOptionalItem(Optional<Item> item, long id) {
        if (item.isEmpty()) {
            throw new ItemNotFoundException("Вещь с id = " + id + " не найдена");
        }
        return item.orElseThrow();
    }

    private Booking handleOptionalBooking(Optional<Booking> booking, long id) {
        if (booking.isEmpty()) {
            throw new BookingNotFoundException("Бронирование с id = " + id + " не найдено");
        }
        return booking.orElseThrow();
    }
}
