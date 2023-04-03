package ru.practicum.shareit.booking;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;

public interface BookingService {

    BookingDtoOut getBookingById(long userId, long bookingId);

    List<BookingDtoOut> getBookingsOfUser(long userId, String state, int from, int size);

    List<BookingDtoOut> getBookingsOfOwner(long userId, String state, int from, int size);

    @Transactional
    BookingDtoOut saveBooking(long userId, BookingDtoIn bookingDtoIn);

    @Transactional
    BookingDtoOut changeStatus(long userId, long bookingId, Boolean approved);

}
