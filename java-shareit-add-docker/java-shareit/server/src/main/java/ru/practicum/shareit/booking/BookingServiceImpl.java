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
    public List<BookingDtoOut> getBookingsOfUser(long userId, String query, int from, int size) {
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
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pagination pageRequest = new Pagination(from,size,sort);
        List<Booking> bookings = getPageBookings(state, user, pageRequest);
        log.info("Получен список бронирования для пользователя id = " + userId + " : " + bookings);
        return bookings.stream()
                .map(x -> BookingDtoMapper.toBookingDtoOut(x, x.getItem()))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoOut> getBookingsOfOwner(long userId, String query, int from, int size) {
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
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pagination pageRequest = new Pagination(from,size,sort);
        List<Booking> bookings = getPageOwnerBookings(state, ownerItems, pageRequest);
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

    private List<Booking> getPageBookings(BookingState state, User user, Pagination pageRequest) {
        List<Booking> page;
        switch (state) {
            case REJECTED:
                page = bookingRepository.findAllByBookerIsAndStatusIs(pageRequest, user, BookingStatus.REJECTED);
                break;
            case WAITING:
                page = bookingRepository.findAllByBookerIsAndStatusIs(pageRequest, user, BookingStatus.WAITING);
                break;
            case CURRENT:
                page = bookingRepository.findAllByBookerAndCurrentOrderByStartDesc(pageRequest, user);
                break;
            case PAST:
                page = bookingRepository.findAllByBookerAndPastOrderByStartDesc(pageRequest, user);
                break;
            case FUTURE:
                page = bookingRepository.findAllByBookerAndFutureOrderByStartDesc(pageRequest, user);
                break;
            default:
                page = bookingRepository.findAllByBooker(pageRequest, user);
                break;
        }
        return page;
    }

    private List<Booking> getPageOwnerBookings(BookingState state, List<Item> items, Pagination pageRequest) {
        List<Booking> page;
        switch (state) {
            case REJECTED:
                page = bookingRepository.findAllByItemInAndStatusIs(pageRequest, items, BookingStatus.REJECTED);
                break;
            case WAITING:
                page = bookingRepository.findAllByItemInAndStatusIs(pageRequest, items, BookingStatus.WAITING);
                break;
            case CURRENT:
                page = bookingRepository.findAllByItemsAndCurrentOrderByStartDesc(pageRequest, items);
                break;
            case PAST:
                page = bookingRepository.findAllByItemAndPastOrderByStartDesc(pageRequest, items);
                break;
            case FUTURE:
                page = bookingRepository.findAllByItemsAndFutureOrderByStartDesc(pageRequest, items);
                break;
            default:
                page = bookingRepository.findAllByItemIn(pageRequest, items);
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
