package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.Pagination;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner() != userId) {
            throw new BookingNotFoundException("Запрос статуса бронирования поступил не от владельца вещи или автора заявки");
        }
        log.info("Получено бронирование id = " + bookingId);
        return BookingDtoMapper.toBookingDtoOut(booking, booking.getItem());
    }

    @Override
    public List<BookingDtoOut> getBookingsOfUser(long userId, String query, Integer from, Integer size) {
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
        User user = handleOptionalUser(userRepository.findById(userId), userId);
        Pageable pageable;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Page<Booking> page;
        Pagination pager = new Pagination(from, size);
        List<Booking> bookings = new ArrayList<>();
        for (int i = pager.getIndex(); i < pager.getTotalPages(); i++) {
            pageable =
                    PageRequest.of(i, pager.getPageSize(), sort);
            page = getPageBookings(state, user, pageable);
            bookings.addAll(page.stream().collect(toList()));
            if (!page.hasNext()) {
                break;
            }
        }
        bookings = bookings.stream().limit(size).collect(toList());
        log.info("Получен список бронирования для пользователя id = " + userId + " : " + bookings);
        return bookings.stream()
                .map(x -> BookingDtoMapper.toBookingDtoOut(x, x.getItem()))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoOut> getBookingsOfOwner(long userId, String query, Integer from, Integer size) {
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
        List<Item> ownerItems = itemRepository.findAllByOwnerIsOrderById(userId);
        Pageable pageable;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Page<Booking> page;
        Pagination pager = new Pagination(from, size);
        List<Booking> bookings = new ArrayList<>();
        for (int i = pager.getIndex(); i < pager.getTotalPages(); i++) {
            pageable = PageRequest.of(i, pager.getPageSize(), sort);
            page = getPageOwnerBookings(state, ownerItems, pageable);
            bookings.addAll(page.stream().collect(toList()));
            if (!page.hasNext()) {
                break;
            }
        }
        bookings = bookings.stream().limit(size).collect(toList());
        log.info("Получен список бронирования для владельца id = " + userId + " : " + bookings);
        return bookings.stream()
                .map(x -> BookingDtoMapper.toBookingDtoOut(x, x.getItem()))
                .collect(Collectors.toList());

    }

    @Override
    public BookingDtoOut saveBooking(long userId, BookingDtoIn bookingDtoIn) {
        log.info("Добавление бронирования " + bookingDtoIn);
        if (bookingDtoIn.getEnd().isBefore(bookingDtoIn.getStart()) || bookingDtoIn.getEnd().isEqual(bookingDtoIn.getStart())) {
            throw new ValidationException("Дата конца бронирования раньше или равна дате начала");
        }
        User user = handleOptionalUser(userRepository.findById(userId), userId);
        Item item = handleOptionalItem(itemRepository.findById(bookingDtoIn.getItemId()), bookingDtoIn.getItemId());
        if (!item.getAvailable()) {
            throw new ValidationException("Товар с id = " + item.getId() + " недоступен для бронирования");
        }
        if (userId == item.getOwner()) {
            throw new ItemNotFoundException("Запрошено бронирование вещи id = " + item.getId() + " владельцем");
        }
        Booking saved = bookingRepository.save(BookingDtoMapper.toBooking(bookingDtoIn, item, user));
        log.info("Сохранено бронирование " + saved + " пользователя с id = " + userId);
        return BookingDtoMapper.toBookingDtoOut(saved, item);
    }

    @Override
    public BookingDtoOut changeStatus(long userId, long bookingId, Boolean approved) {
        log.info("Обновление статуса бронирования с id = " + bookingId);
        handleOptionalUser(userRepository.findById(userId), userId);
        Booking booking = handleOptionalBooking(bookingRepository.findById(bookingId), bookingId);
        if (booking.getItem().getOwner() != userId) {
            throw new BookingNotFoundException("Запрос на изменение статуса бронирования поступил не от владельца вещи");
        }
        BookingStatus bookingStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        if (bookingStatus == BookingStatus.APPROVED && booking.getStatus() == BookingStatus.APPROVED) {
            throw new ValidationException("Бронирование id = " + bookingId + " уже было подтвреждено");
        }
        bookingRepository.changeStatusById(bookingStatus, bookingId);
        log.info("Обновлен статус бронирования с id = " + bookingId);
        Booking newBooking = handleOptionalBooking(bookingRepository.findById(bookingId), bookingId);
        return BookingDtoMapper.toBookingDtoOut(newBooking, booking.getItem());
    }

    private Page<Booking> getPageBookings(BookingState state, User user, Pageable pageable) {
        Page<Booking> page;
        switch (state) {
            case REJECTED:
                page = bookingRepository.findAllByBookerIsAndStatusIsOrderByStartDesc(user, BookingStatus.REJECTED, pageable);
                break;
            case WAITING:
                page = bookingRepository.findAllByBookerIsAndStatusIsOrderByStartDesc(user, BookingStatus.WAITING, pageable);
                break;
            case CURRENT:
                page = bookingRepository.findAllByBookerAndCurrentOrderByStartDesc(user, pageable);
                break;
            case PAST:
                page = bookingRepository.findAllByBookerAndPastOrderByStartDesc(user, pageable);
                break;
            case FUTURE:
                page = bookingRepository.findAllByBookerAndFutureOrderByStartDesc(user, pageable);
                break;
            default:
                page = bookingRepository.findAllByBookerOrderByStartDesc(user, pageable);
                break;
        }
        return page;
    }

    private Page<Booking> getPageOwnerBookings(BookingState state, List<Item> items, Pageable pageable) {
        Page<Booking> page;
        switch (state) {
            case REJECTED:
                page = bookingRepository.findAllByItemInAndStatusIsOrderByStartDesc(items, BookingStatus.REJECTED, pageable);
                break;
            case WAITING:
                page = bookingRepository.findAllByItemInAndStatusIsOrderByStartDesc(items, BookingStatus.WAITING, pageable);
                break;
            case CURRENT:
                page = bookingRepository.findAllByItemsAndCurrentOrderByStartDesc(items, pageable);
                break;
            case PAST:
                page = bookingRepository.findAllByItemAndPastOrderByStartDesc(items, pageable);
                break;
            case FUTURE:
                page = bookingRepository.findAllByItemsAndFutureOrderByStartDesc(items, pageable);
                break;
            default:
                page = bookingRepository.findAllByItemInOrderByStartDesc(items, pageable);
                break;
        }
        return page;
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

    private Booking handleOptionalBooking(Optional<Booking> booking, long id) {
        if (booking.isEmpty()) {
            throw new BookingNotFoundException("Бронирование с id = " + id + " не найдено");
        }
        return booking.orElseThrow();
    }
}
