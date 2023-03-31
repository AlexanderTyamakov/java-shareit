package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private UserDto userDto1 = new UserDto(101L, "Alex", "alex@alex.ru");
    private UserDto userDto2 = new UserDto(102L, "Egor", "egor@egor.ru");
    private ItemDto itemDto1 = new ItemDto(101L, "Item1", "Description1", true,
            null, null, null, null);

    @Test
    void shouldGetExceptionWhenSaveBookingByOwnerItem() {
        UserDto ownerDto = userService.saveUser(userDto1);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2025, 12, 25, 12, 00, 00),
                LocalDateTime.of(2025, 12, 26, 12, 00, 00),
                newItemDto.getId());
        ItemNotFoundException exp = assertThrows(ItemNotFoundException.class,
                () -> bookingService.saveBooking(ownerDto.getId(),bookingDtoIn));
        assertEquals("Запрошено бронирование вещи id = " + newItemDto.getId() + " владельцем", exp.getMessage());
    }

    @Test
    void shouldGetExceptionWhenGetBookingByNotOwnerOrNotBooker() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        UserDto userDto3 = new UserDto(103L, "Alex", "alexd@alex.ru");
        userDto3 = userService.saveUser(userDto3);
        Long userId = userDto3.getId();
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2025, 12, 25, 12, 00, 00),
                LocalDateTime.of(2025, 12, 26, 12, 00, 00),
                newItemDto.getId());
        BookingDtoOut bookingDto = bookingService.saveBooking(newUserDto.getId(), bookingDtoIn);
        BookingNotFoundException exp = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(userId,bookingDto.getId()));
        assertEquals("Запрос статуса бронирования поступил не от владельца вещи или автора заявки",
                exp.getMessage());
    }

    @Test
    void shouldReturnBookingWhenGetBookingByBooker() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        UserDto userDto3 = new UserDto(103L, "Alex", "alexd@alex.ru");
        userDto3 = userService.saveUser(userDto3);
        Long userId = userDto3.getId();
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2025, 12, 25, 12, 00, 00),
                LocalDateTime.of(2025, 12, 26, 12, 00, 00),
                newItemDto.getId());
        BookingDtoOut bookingDto = bookingService.saveBooking(newUserDto.getId(), bookingDtoIn);
        BookingDtoOut returned = bookingService.getBookingById(newUserDto.getId(),bookingDto.getId());
        assertEquals(bookingDto.getId(),returned.getId());
    }

    @Test
    void shouldReturnBookingWhenGetBookingByOwner() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        UserDto userDto3 = new UserDto(103L, "Alex", "alexd@alex.ru");
        userDto3 = userService.saveUser(userDto3);
        Long userId = userDto3.getId();
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2025, 12, 25, 12, 00, 00),
                LocalDateTime.of(2025, 12, 26, 12, 00, 00),
                newItemDto.getId());
        BookingDtoOut bookingDto = bookingService.saveBooking(newUserDto.getId(), bookingDtoIn);
        BookingDtoOut returned = bookingService.getBookingById(ownerDto.getId(),bookingDto.getId());
        assertEquals(bookingDto.getId(),returned.getId());
    }

    @Test
    void shouldReturnExceptionWhenGetBookingsByBookerAndInvalidState() {
        UserDto newUserDto = userService.saveUser(userDto2);
        ValidationException exp = assertThrows(ValidationException.class,
                () -> bookingService.getBookingsOfUser(newUserDto.getId(), "SHOW ME",0, null));
        assertEquals("Unknown state: SHOW ME", exp.getMessage());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByBookerAndSizeIsNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2025, 12, 25, 12, 00, 00),
                LocalDateTime.of(2025, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.of(2026, 12, 25, 12, 00, 00),
                LocalDateTime.of(2026, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfUser(newUserDto.getId(), "ALL",0, null);
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByBookerAndSizeIsNotNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2025, 12, 25, 12, 00, 00),
                LocalDateTime.of(2025, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.of(2026, 12, 25, 12, 00, 00),
                LocalDateTime.of(2026, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfUser(newUserDto.getId(), "ALL", 0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsInWaitingStatusByBookerAndSizeIsNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2025, 12, 25, 12, 00, 00),
                LocalDateTime.of(2025, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.of(2026, 12, 25, 12, 00, 00),
                LocalDateTime.of(2026, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfUser(newUserDto.getId(), "WAITING",
                0, null);
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsInWaitingStatusByBookerAndSizeNotNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2025, 12, 25, 12, 00, 00),
                LocalDateTime.of(2025, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.of(2026, 12, 25, 12, 00, 00),
                LocalDateTime.of(2026, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfUser(newUserDto.getId(),"WAITING",
                0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsInRejectedStatusByBookerAndSizeIsNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2025, 12, 25, 12, 00, 00),
                LocalDateTime.of(2025, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.of(2026, 12, 25, 12, 00, 00),
                LocalDateTime.of(2026, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfUser(newUserDto.getId(),"REJECTED",
                0, null);
        assertEquals(0, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsInRejectedStatusByBookerAndSizeNotNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2025, 12, 25, 12, 00, 00),
                LocalDateTime.of(2025, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.of(2026, 12, 25, 12, 00, 00),
                LocalDateTime.of(2026, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfUser(newUserDto.getId(),"REJECTED",
                0, 1);
        assertEquals(0, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsInCurrentStatusByBookerAndSizeIsNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.now().minus(10, ChronoUnit.MINUTES),
                LocalDateTime.now().plus(10, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.now().minus(11, ChronoUnit.MINUTES),
                LocalDateTime.now().plus(11, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfUser(newUserDto.getId(),"CURRENT",
                0, null);
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsInCurrentStatusByBookerAndSizeNotNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.now().minus(10, ChronoUnit.MINUTES),
                LocalDateTime.now().plus(10, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.now().minus(11, ChronoUnit.MINUTES),
                LocalDateTime.now().plus(11, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfUser(newUserDto.getId(),"CURRENT",
                0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsInPastStatusByBookerAndSizeIsNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.now().minus(10, ChronoUnit.MINUTES),
                LocalDateTime.now().minus(5, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.now().minus(11, ChronoUnit.MINUTES),
                LocalDateTime.now().minus(6, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfUser(newUserDto.getId(),"PAST",
                0, null);
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsInPastStatusByBookerAndSizeNotNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.now().minus(10, ChronoUnit.MINUTES),
                LocalDateTime.now().minus(5, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.now().minus(11, ChronoUnit.MINUTES),
                LocalDateTime.now().minus(6, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfUser(newUserDto.getId(),"PAST",
                0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsInFutureStatusByBookerAndSizeIsNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.now().plus(10, ChronoUnit.MINUTES),
                LocalDateTime.now().plus(15, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.now().plus(11, ChronoUnit.MINUTES),
                LocalDateTime.now().plus(16, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfUser(newUserDto.getId(),"FUTURE",
                0, null);
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsInFutureStatusByBookerAndSizeNotNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.now().plus(10, ChronoUnit.MINUTES),
                LocalDateTime.now().plus(15, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.now().plus(11, ChronoUnit.MINUTES),
                LocalDateTime.now().plus(16, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfUser(newUserDto.getId(),"FUTURE",
                0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    void shouldReturnExceptionWhenGetBookingsByOwnerAndInvalidState() {
        UserDto newUserDto = userService.saveUser(userDto2);
        ValidationException exp = assertThrows(ValidationException.class,
                () -> bookingService.getBookingsOfOwner(newUserDto.getId(), "SHOW ME",0, null));
        assertEquals("Unknown state: SHOW ME", exp.getMessage());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByOwnerAndSizeIsNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2025, 12, 25, 12, 00, 00),
                LocalDateTime.of(2025, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.of(2026, 12, 25, 12, 00, 00),
                LocalDateTime.of(2026, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfOwner(ownerDto.getId(),"ALL",
                0, null);
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByOwnerAndSizeNotNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2025, 12, 25, 12, 00, 00),
                LocalDateTime.of(2025, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.of(2026, 12, 25, 12, 00, 00),
                LocalDateTime.of(2026, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfOwner(ownerDto.getId(),"ALL",
                0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByOwnerAndStatusWaitingAndSizeIsNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2025, 12, 25, 12, 00, 00),
                LocalDateTime.of(2025, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.of(2026, 12, 25, 12, 00, 00),
                LocalDateTime.of(2026, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfOwner(ownerDto.getId(),"WAITING",
                0, null);
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByOwnerAndStatusWaitingAndSizeNotNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2025, 12, 25, 12, 00, 00),
                LocalDateTime.of(2025, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.of(2026, 12, 25, 12, 00, 00),
                LocalDateTime.of(2026, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfOwner(ownerDto.getId(),"WAITING",
                0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByOwnerAndStatusRejectedAndSizeIsNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2025, 12, 25, 12, 00, 00),
                LocalDateTime.of(2025, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.of(2026, 12, 25, 12, 00, 00),
                LocalDateTime.of(2026, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfOwner(ownerDto.getId(),"REJECTED",
                0, null);
        assertEquals(0, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByOwnerAndStatusRejectedAndSizeNotNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2025, 12, 25, 12, 00, 00),
                LocalDateTime.of(2025, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.of(2026, 12, 25, 12, 00, 00),
                LocalDateTime.of(2026, 12, 26, 12, 00, 00),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfOwner(ownerDto.getId(),"REJECTED",
                0, 1);
        assertEquals(0, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByOwnerInCurrentStatusAndSizeIsNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.now().minus(10, ChronoUnit.MINUTES),
                LocalDateTime.now().plus(10, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.now().minus(11, ChronoUnit.MINUTES),
                LocalDateTime.now().plus(11, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfOwner(ownerDto.getId(),"CURRENT",
                0, null);
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByOwnerInCurrentStatusAndSizeNotNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.now().minus(10, ChronoUnit.MINUTES),
                LocalDateTime.now().plus(10, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.now().minus(11, ChronoUnit.MINUTES),
                LocalDateTime.now().plus(11, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfOwner(ownerDto.getId(),"CURRENT",
                0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByOwnerInPastStatusAndSizeIsNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.now().minus(10, ChronoUnit.MINUTES),
                LocalDateTime.now().minus(5, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.now().minus(11, ChronoUnit.MINUTES),
                LocalDateTime.now().minus(6, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfOwner(ownerDto.getId(),"PAST",
                0, null);
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByOwnerInPastStatusAndSizeNotNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.now().minus(10, ChronoUnit.MINUTES),
                LocalDateTime.now().minus(5, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.now().minus(11, ChronoUnit.MINUTES),
                LocalDateTime.now().minus(6, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfOwner(ownerDto.getId(),"PAST",
                0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByOwnerInFutureStatusAndSizeIsNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.now().plus(10, ChronoUnit.MINUTES),
                LocalDateTime.now().plus(15, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.now().plus(11, ChronoUnit.MINUTES),
                LocalDateTime.now().plus(16, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfOwner(ownerDto.getId(),"FUTURE",
                0, null);
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByOwnerInFutureStatusAndSizeNotNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.now().plus(10, ChronoUnit.MINUTES),
                LocalDateTime.now().plus(15, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn);
        BookingDtoIn bookingDtoIn2 = new BookingDtoIn(
                LocalDateTime.now().plus(11, ChronoUnit.MINUTES),
                LocalDateTime.now().plus(16, ChronoUnit.MINUTES),
                newItemDto.getId());
        bookingService.saveBooking(newUserDto.getId(),bookingDtoIn2);
        List<BookingDtoOut> listBookings = bookingService.getBookingsOfOwner(ownerDto.getId(),"FUTURE",
                0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    void shouldChangeBookingStatusApprovedByOwner() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2025, 12, 25, 12, 00, 00),
                LocalDateTime.of(2025, 12, 26, 12, 00, 00),
                newItemDto.getId());
        BookingDtoOut bookingDtoOut = bookingService.saveBooking(newUserDto.getId(), bookingDtoIn);
        BookingDtoOut status = bookingService.changeStatus(ownerDto.getId(),bookingDtoOut.getId(),true);
        assertEquals(BookingStatus.APPROVED, status.getStatus());
    }

    @Test
    void shouldChangeBookingStatusRejectedByOwner() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2025, 12, 25, 12, 00, 00),
                LocalDateTime.of(2025, 12, 26, 12, 00, 00),
                newItemDto.getId());
        BookingDtoOut bookingDtoOut = bookingService.saveBooking(newUserDto.getId(), bookingDtoIn);
        BookingDtoOut status = bookingService.changeStatus(ownerDto.getId(),bookingDtoOut.getId(),false);
        assertEquals(BookingStatus.REJECTED, status.getStatus());
    }

    @Test
    void shouldGetExceptionWhenChangeBookingStatusByUser() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(),itemDto1);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2025, 12, 25, 12, 00, 00),
                LocalDateTime.of(2025, 12, 26, 12, 00, 00),
                newItemDto.getId());
        BookingDtoOut bookingDtoOut = bookingService.saveBooking(newUserDto.getId(), bookingDtoIn);
        BookingNotFoundException exp = assertThrows(BookingNotFoundException.class,
                () -> bookingService.changeStatus(newUserDto.getId(),bookingDtoOut.getId(),true));
        assertEquals("Запрос на изменение статуса бронирования поступил не от владельца вещи", exp.getMessage());
    }
}