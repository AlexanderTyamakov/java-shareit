package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query(value = "update Booking b set b.status = ?1 where b.id = ?2")
    void changeStatusById(BookingStatus approved, long bookingId);

    @Query(value = "select b from Booking b where b.booker = ?1 and " +
            " b.start > CURRENT_TIMESTAMP and b.end < CURRENT_TIMESTAMP order by b.start desc ")
    List<Booking> findAllByBookerAndCurrentOrderByStartDesc(long userId);

    @Query(value = "select b from Booking b where b.booker = ?1 and " +
            " b.start > CURRENT_TIMESTAMP order by b.start desc ")
    List<Booking> findAllByBookerAndFutureOrderByStartDesc(long userId);

    @Query(value = "select b from Booking b where b.booker = ?1 and " +
            " b.end < CURRENT_TIMESTAMP order by b.start desc ")
    List<Booking> findAllByBookerAndPastOrderByStartDesc(long userId);

    @Query(value = "select b from Booking b where b.item in (?1) and " +
            " b.start < CURRENT_TIMESTAMP and b.end > CURRENT_TIMESTAMP order by b.start desc ")
    List<Booking> findAllByItemsAndCurrentOrderByStartDesc(List<Long> items);

    @Query(value = "select b from Booking b where b.item in (?1) and " +
            " b.start > CURRENT_TIMESTAMP order by b.start desc ")
    List<Booking> findAllByItemsAndFutureOrderByStartDesc(List<Long> items);

    @Query(value = "select b from Booking b where b.item in (?1) and " +
            " b.end < CURRENT_TIMESTAMP order by b.start desc ")
    List<Booking> findAllByItemAndPastOrderByStartDesc(List<Long> items);

    @Query(value = "select b from Booking b where b.item in (?1) and " +
            "b.status not in (?2) and b.start > CURRENT_TIMESTAMP order by b.start asc ")
    List<Booking> findNextBooking(Long item, BookingStatus bookingStatus);

    @Query(value = "select b from Booking b where b.item = ?1 and " +
            " b.status not in (?2) and b.end < CURRENT_TIMESTAMP order by b.start desc ")
    List<Booking> findLastBooking(long itemId, BookingStatus bookingStatus);

    @Query(value = "select b from Booking b where b.item = ?1 and " +
            " b.booker = ?2 and b.status not in (?3) and b.end < CURRENT_TIMESTAMP ")
    List<Booking> findByItemAndBookerAndStatus (long itemId, long userId, BookingStatus bookingStatus);
}
