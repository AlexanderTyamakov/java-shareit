package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {

    List<Booking> findAllByBookerIsOrderByStartDesc(long id);

    List<Booking> findAllByBookerIsAndStatusIsOrderByStartDesc(long id, BookingStatus bookingStatus);

    List<Booking> findAllByItemInAndStatusIsOrderByStartDesc(List<Long> items, BookingStatus bookingStatus);

    List<Booking> findAllByItemInOrderByStartDesc(List<Long> items);
}
