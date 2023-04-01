package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDtoOut getBookingById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoOut> getBookingsOfUser(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(required = false) String state,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.getBookingsOfUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getBookingsOfOwner(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(required = false) String state,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.getBookingsOfOwner(userId, state, from, size);
    }

    @PostMapping
    public BookingDtoOut saveBooking(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Validated BookingDtoIn bookingDtoIn) {
        return bookingService.saveBooking(userId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut changeStatus(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long bookingId, @RequestParam Boolean approved) {
        return bookingService.changeStatus(userId, bookingId, approved);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final ValidationException e) {
        return new ErrorResponse(
                "Ошибка в отправленных данных", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final NotFoundException e) {
        return new ErrorResponse(
                "Отсутствует объект", e.getMessage()
        );
    }
}
