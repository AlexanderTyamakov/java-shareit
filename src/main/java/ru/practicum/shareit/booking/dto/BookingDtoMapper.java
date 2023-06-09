package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.user.User;

public class BookingDtoMapper {

    public static BookingDtoOut toBookingDtoOut(Booking booking, Item item) {
        return new BookingDtoOut(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemDtoMapper.toItemDtoShort(item),
                BookingDtoMapper.toBookerDto(booking),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDtoIn bookingDtoIn, Item item, User booker) {
        return new Booking(
                null,
                bookingDtoIn.getStart(),
                bookingDtoIn.getEnd(),
                item,
                booker,
                BookingStatus.WAITING);
    }

    public static BookerDto toBookerDto(Booking booking) {
        return new BookerDto(
                booking.getBooker().getId()
        );
    }

    public static LastBookingDto lastBookingDto(Booking booking) {
        return new LastBookingDto(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }

    public static NextBookingDto nextBookingDto(Booking booking) {
        return new NextBookingDto(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }
}
