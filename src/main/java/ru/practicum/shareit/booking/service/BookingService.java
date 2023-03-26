package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;

public interface BookingService {

    BookingDtoOut getBookingById(long userId, long bookingId);

    List<BookingDtoOut> getBookingsOfUser(long userId, String state);

    List<BookingDtoOut> getBookingsOfOwner(long userId, String state);

    @Transactional
    BookingDtoOut saveBooking(long userId, BookingDtoIn bookingDtoIn);

    @Transactional
    BookingDtoOut changeStatus(long userId, long bookingId, Boolean approved);

}
